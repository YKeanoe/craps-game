package controller;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import model.ClientGameEngineCallbackServer;
import model.GameEngineCallbackImpl;
import model.GameEngineClientStub;
import model.GameEngineImpl;
import model.GameEngineServerStub;
import model.SimplePlayer;
import model.interfaces.GameEngine;
import model.interfaces.GameEngineCallback;
import model.interfaces.Player;
import view.GameWindow;

public class CasinoWheelController
{
	final GameEngineClientStub gameEngine;
	GameEngineCallback gameEngineCallback;
	int WHEEL_SIZE = 40;
	int currentWheel = 0;
	CasinoWheelController controller = this;
	GameWindow mainWindow;
	
	public CasinoWheelController(Socket socket){
      
		gameEngine = new GameEngineClientStub(socket, controller);
      new Thread(gameEngine).start();

		/* Load main menu frame */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try { 
					mainWindow = new GameWindow(controller);
					mainWindow.setLocationRelativeTo(null);
					mainWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
		
	/* addPlayer function is used to add a player to the game engine. 
	 * First, it get all the players from the game engine. Then it check if
	 * there is no player yet, create a new player as player 1. If not, it will
	 * search for the highest ID and use the next ID as the new player.
	 * After an ID is found, it call the game engine to add the player. */
	public void addPlayer(String name, int point){   
	   ArrayList<Player> players = getAllPlayers();
	   int id = 0;
	   if(!players.isEmpty()){
	      for(int i = 0 ; i < players.size(); i++){
	         if(Integer.parseInt(players.get(i).getPlayerId()) >= id){
	            id = Integer.parseInt(players.get(i).getPlayerId()) + 1;
	         }
	      }
	   } else
	      id = 1;
//	   int id = gameEngine.getPlayerPool();
		Player player = new SimplePlayer(Integer.toString(id), name, point);
		gameEngine.addPlayer(player);
	}
	
	/* changeWheelSize function is used to change the current wheel size */
	public void changeWheelSize(int size){
      WHEEL_SIZE = size;
   }
	
	/* getAllPlayers function is used to get all players from the game engine 
    * and return it as an ArrayList. */
   public ArrayList<Player> getAllPlayers(){
      return new ArrayList<Player>(gameEngine.getAllPlayers());
   }
   
   /* getWheelSize function return the wheel size */
   public int getWheelSize(){
      return WHEEL_SIZE;
   }
   
	/* Place bet function is used to place a bet from the GUI to the game engine */
	public void placeBet(String playerId, int number, int bet){
		Player player = gameEngine.getPlayer(playerId);		
		gameEngine.placeBet(player, number, bet);
	}

	/* refreshBet is a function used to reset the bet and number bet of all players. */
   public void refreshBet(){
      ArrayList<Player> players = controller.getAllPlayers();
      for(int i = 0; i < players.size(); i++){
         players.get(i).resetBet();
      }
   }
   
	/* removePlayer function is used to pass the id from GUI to the game engine */
	public void removePlayer(String id){
	   gameEngine.removePlayer(gameEngine.getPlayer(id));
	}
		
	public GameEngineClientStub getGameEngine(){
	   return gameEngine;
	}
	
	public Player getMainPlayer(){
	   return gameEngine.getMainPlayer();
	}
	
	/* spin function is use to spin the wheel from the game engine. First, it 
	 * create a new GameEngineCallbackImpl and pass the game window and add it 
	 * to the game engine. Then it call the spin function from the game engine
	 * using the wheel size. Then it will call the game engine to calculate the
	 * result. The spin function will also return the result number for the GUI. */
	public int Spin(){
		gameEngineCallback = new ClientGameEngineCallbackServer(mainWindow);
		gameEngine.addGameEngineCallback(gameEngineCallback);
		int result = gameEngine.spin(WHEEL_SIZE, 1, 300, 10);
		gameEngine.calculateResult(result);
		for (Player player : gameEngine.getAllPlayers()){
			   System.out.println(player);
			}		
		return result;
	}

   public GameWindow getWindow()
   {
      // TODO Auto-generated method stub
      return mainWindow;
   }
	
}
