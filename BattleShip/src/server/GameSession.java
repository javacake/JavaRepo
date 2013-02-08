package server;

import java.io.*;
import java.net.*;
import java.util.Date;

// Define the thread class for handling a new game session for two players

/**
 * The GameSession class. this will hold two players and manage game play between them.
 */
class GameSession {

	/** The parent refernce. */
	private ServerFrame sframe;
	
	//comunication
	/** The player1. */
	private Player player1;
	
	/** The player2. */
	private Player player2;
	
	/** The player1 reader thread. */
	private Thread player1Reader;
	
	/** The player2 reader thread. */
	private Thread player2Reader;	
	
	// Continue to play
	/** The continue to play flag. */
	private boolean continueToPlay;
	
	/** The array of player ids. */
	private boolean[] playerIds;
	

  /**
   * Construct game session thread.
   *
   * @param sf parent reference.
   */
	public GameSession(ServerFrame sf) {
		sframe = sf;
		playerIds = new boolean[]{false,false};
		continueToPlay = false;
	}

	/**
	 * When new players joins session.
	 *
	 * @param pl socket reference of the player
	 * @return true, if player joins sessions.
	 */
	protected boolean playerJoined(Socket pl){
		//serverLog.addElement(new Date() + ": New Player joined " + '\n');
		
		if(player1==null){
			player1 = new Player(GameConstants.PLAYERONE,pl,this);
			sframe.serverLog.addElement(new Date() + ": Player 1 joined session " + '\n');
			sframe.serverLog.addElement("Player 1's IP address" + pl.getInetAddress().getHostAddress() + '\n');
			playerIds[GameConstants.PLAYERONE] = true;

	        player1Reader = new Thread(new IncomingReader(this, player1.getFromPlayer(), player1.getId()));
	        player1Reader.start();
			
	        //Update player
	        Message msg = new Message();
	        msg.setMessageType(PlayerStatus.CONNECTED);
	        gameAction(player1.getId(),msg);
	        //player1.setStatus(checkOpponent(ServerConstants.PLAYERONE));
	        //playerConnectEvent(ServerConstants.PLAYERONE);
	        
	        return true;

		}
		else if(player2==null){
			player2 = new Player(GameConstants.PLAYERTWO,pl,this);
			sframe.serverLog.addElement(new Date() + ": Player 2 joined session " + '\n');
			sframe.serverLog.addElement("Player 2's IP address" + pl.getInetAddress().getHostAddress() + '\n');
			playerIds[GameConstants.PLAYERTWO] = true;

	        player2Reader = new Thread(new IncomingReader(this, player2.getFromPlayer(), player2.getId()));
	        player2Reader.start();
	        
	        //Update player
	        Message msg = new Message();
	        msg.setMessageType(PlayerStatus.CONNECTED);
	        gameAction(player2.getId(),msg);
	        //playerConnectEvent(ServerConstants.PLAYERTWO);
	        
	        return true;
  
		}
		
		return false;
		
	}

	/**
	 * Handle Player connection errors.
	 *
	 * @param pId id of the player.
	 */
	protected void playerDisconnect(int pId){
		if(player1.getId()==pId) player1 = null;
		else if (player2.getId()==pId) player2 = null;
		
		playerIds[pId] = false;
		
		sframe.serverLog.addElement(new Date() +" player "+ pId + ": disconnected");
	}
	
	/**
	 * Message object received from player.
	 *
	 * @param fromPlayerId id of the sender player
	 * @param msg Message object.
	 */
	protected synchronized void messageReceived(int fromPlayerId, Object msg){
		//serverLog.addElement(new Date() + "Message received: " + msg.toString());
		
		//parse and update Game
		Message cmsg = (Message) msg;		
		//updateGame(fromPlayerId, cmsg);
		gameAction(fromPlayerId, cmsg);
	}

	
	/**
	 * Take action on received Message from the players.
	 *
	 * @param pId the id of sender
	 * @param clmsg Message object sent
	 */
	private void gameAction(int pId, Message clmsg){
		Player player;
		Player opponent;
		
		if(GameConstants.PLAYERONE == pId){
			player = player1;
			opponent = player2;
		}else{
			player = player2;
			opponent = player1;			
		}
			
		switch(clmsg.getMessageType()){
		case CONNECTED:
			if(opponent==null){
				//send opponent is not connected to player
				Message sm = new Message();
				sm.setMessageType(PlayerStatus.NOT_CONNECTED);
				sendToPlayer(player.getId(),sm);
			}
			else if(opponent.getStatus() == PlayerStatus.CONNECTED){
				//send opponent is connected to player
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.CONNECTED);
				sendToPlayer(player.getId(),sm1);
				//send player is connected to opponent
				sendToPlayer(opponent.getId(),sm1);
				
			}else if(opponent.getStatus() == PlayerStatus.READY){
				//send opponent board to player
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.READY);
				sm1.setShipPosition(opponent.getPlayerBoard());
				sm1.setShipLocation(opponent.getShips());
				sendToPlayer(player.getId(),sm1);
				
				//send player is connected to opponent
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.CONNECTED);
				sendToPlayer(opponent.getId(),sm2);				
				
			}
			break;
		case READY:

			player.setStatus(PlayerStatus.READY);
			player.setPlayerBoard(clmsg.getShipPosition());
			player.setShips(clmsg.getShipLocation());
			
			if(opponent==null){
				//send not connected
				Message sm = new Message();
				sm.setMessageType(PlayerStatus.NOT_CONNECTED);
				sendToPlayer(player.getId(),sm);
			}
			else if(opponent.getStatus() == PlayerStatus.CONNECTED){
				//send connected
				//save ships
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.CONNECTED);
				sendToPlayer(player.getId(),sm1);
				
				//send player is connected to opponent
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.READY);
				sm2.setShipPosition(player.getPlayerBoard());
				sm2.setShipLocation(player.getShips());
				sendToPlayer(opponent.getId(),sm2);						
				
				
			}else if(opponent.getStatus() == PlayerStatus.READY){
				//start game			
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.WAIT); //First wait, start game
				sendToPlayer(player.getId(),sm1);				
				
				//send player board to opponent
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.TURN); //First turn, start game
				sm2.setShipPosition(player.getPlayerBoard());
				sm2.setShipLocation(player.getShips());
				sendToPlayer(opponent.getId(),sm2);
				
				continueToPlay = true;
				//Change first player to Turn - send turn to first
				//Change second to wait - send wait to second
			}
			break;
		case TURN:
			//Player had done his turn and destroyed ship
			
			player.setHitAtCell(clmsg.getRow(),clmsg.getColumn());
			
			if(clmsg.isFlag()){
				
				if(clmsg.isShipDestroyed()){
					player.setShipsDestroyed();
					
					if(player.getShipsDestroyed()==5){
						//Game over
						//send winner msg to both players
						player.setStatus(PlayerStatus.GAMEOVER);
						opponent.setStatus(PlayerStatus.GAMEOVER);
						
						Message sm1 = new Message();
						sm1.setMessageType(PlayerStatus.GAMEOVER);
						sm1.setFlag(true); //Player wins
						sendToPlayer(player.getId(),sm1);				
						
						//send player board to opponent
						Message sm2 = new Message();
						sm2.setMessageType(PlayerStatus.GAMEOVER);
						sm2.setFlag(false); //Opponent loses
						sendToPlayer(opponent.getId(),sm2);
						
						resetPlayer();
						
						return;
					}
				}
				//send turn
				player.setStatus(PlayerStatus.TURN);				
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.TURN);
				sm1.setFlag(false); //without coordinates
				sendToPlayer(player.getId(),sm1);				
				
				//send player board to opponent
				opponent.setStatus(PlayerStatus.WAIT);				
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.WAIT);
				sm2.setRow(clmsg.getRow());
				sm2.setColumn(clmsg.getColumn());
				sm2.setFlag(true); //with coordinates
				sendToPlayer(opponent.getId(),sm2);				
				
			}else{
				player.setStatus(PlayerStatus.WAIT);
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.WAIT);
				sm1.setFlag(false); //without coordinates
				sendToPlayer(player.getId(),sm1);				
				
				//send player board to opponent
				opponent.setStatus(PlayerStatus.TURN);
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.TURN);
				sm2.setRow(clmsg.getRow());
				sm2.setColumn(clmsg.getColumn());
				sm2.setFlag(true); //with coordinates
				sendToPlayer(opponent.getId(),sm2);
			}			

			break;
			
		case WAIT:
			//Player had done his turn
			if(opponent.getStatus() == PlayerStatus.TURN){
				
			}
			else if(opponent.getStatus() == PlayerStatus.GAMEOVER){
				//Do nothing
			}
			
			break;
			
		case GAMEOVER:
			if(opponent.getStatus() == PlayerStatus.GAMEOVER){
				
				Message sm1 = new Message();
				sm1.setMessageType(PlayerStatus.GAMEOVER);
				sm1.setFlag(true); //Player wins
				sendToPlayer(player.getId(),sm1);				
				
				//send player board to opponent
				Message sm2 = new Message();
				sm2.setMessageType(PlayerStatus.GAMEOVER);
				sm2.setFlag(false); //Opponent loses
				sendToPlayer(opponent.getId(),sm2);
				
				continueToPlay = false;
			}			
		}
	}
	
	/**
	 * Reset players of the session.
	 */
	private void resetPlayer(){
		player1.reset();
		player2.reset();
	}
	
	/**
	 * Send Message object to player.
	 *
	 * @param pId the id of player
	 * @param message object to send
	 */
	private void sendToPlayer(int pId, Message message){		

		try {
			
			if(GameConstants.PLAYERONE == pId){
				player1.getToPlayer().writeObject(message);
				player1.getToPlayer().flush();
			}
			else if(GameConstants.PLAYERTWO == pId){
				player2.getToPlayer().writeObject(message);
				player2.getToPlayer().flush();				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

