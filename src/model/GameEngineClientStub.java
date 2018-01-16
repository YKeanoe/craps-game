package model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import controller.CasinoWheelController;
import model.interfaces.GameEngine;
import model.interfaces.GameEngineCallback;
import model.interfaces.Player;

public class GameEngineClientStub implements GameEngine, Runnable
{
   GameEngineCallback gameEngineCallback;
   CasinoWheelController controller;
   HashMap<String, Player> players = new HashMap<>();
   Socket socket;
   Player mainPlayer;

   public GameEngineClientStub(Socket socket, CasinoWheelController controller) {
      this.socket = socket;
      this.controller = controller;
   }

   /* spin function is core mechanic of the game. First, it will generate 
    * a random number from 1 to wheelSize, and increment it for every 
    * delayIncrement. The thread will sleep for every delay (which is the
    * initial delay + increment) which will create a good spin effect. */
   @Override
   public int spin(int wheelSize, int initialDelay, int finalDelay,
                   int delayIncrement)
   {
      if(delayIncrement == 0)
         throw new IllegalArgumentException("Pre-condition: delayIncrement==null"); 
      if(initialDelay >= finalDelay)
         throw new IllegalArgumentException("Pre-condition: initialDelay >= finalDelay"); 

      int startWheel = (int) (Math.random() * wheelSize + 1);
      int wheel = startWheel;
      int delay = initialDelay;
      while(delay <= finalDelay){
         gameEngineCallback.nextNumber(wheel, this);
         delay += delayIncrement;
         wheel ++;
         if(wheel > wheelSize)
            wheel = 1;
         try {
            Thread.sleep(delay);
         }
         catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      gameEngineCallback.result(wheel, this);
      assert (startWheel != wheel ) : "Post-condition: wheel == wheel";
      return wheel;
   }

   /* addPlayer function adds player to the hashmap and mainPlayer */
   @Override
   public void addPlayer(Player player) {
      if(mainPlayer == null){
         players.put(player.getPlayerId(), player);
         mainPlayer = player;
         sendUpdatePlayer();
      }
   }
   
   /* addMultiPlayer adds players from the server */
   public void addMultiPlayer(Player player){
      players.put(player.getPlayerId(), player);
   }

   /* removePlayer function checks if there is player's hashmap, then remove 
    * player. */
   @Override
   public boolean removePlayer(Player player)
   {
      if(players.size() == 0)
         return false;
      
	   if(players.get(player.getPlayerId()) == null){
		   return false;
	   }
	   else{
		   players.remove(player.getPlayerId());
		   return true;
	   }
   }

   /* getPlayer function returns player object. */
   @Override
   public Player getPlayer(String id) {
      return players.get(id);
   }

   /* placeBet function check if player have enough points to bet, if yes,
    * place the bet. */
   @Override
   public boolean placeBet(Player player, int number, int bet) {
      if(player.getPoints() < bet)
         return false;
      player.placeBet(number, bet);
      sendUpdatePlayer();
      return true;
   }

   /* calculateResult function checks every players if they had bet.
    * For every players that bet, if they win, it will add their points
    * double their bet. If they lose, it will simply decrease their points. */
   @Override
   public void calculateResult(int result) {
	   for(Player player : players.values()) {
		   if(mainPlayer.getBet() > 0){
			   int points = mainPlayer.getPoints();
			   int bet = mainPlayer.getBet();
			   int pool;
		
			   if(mainPlayer.getNumber() == result)
				   pool = points + (bet * 2);
			   else
				   pool = points - bet;
		
			   mainPlayer.setPoints(pool);
		   }
		   if(mainPlayer.getPoints() <= 0)
			   removePlayer(mainPlayer);
	   }
   }

   /* getAllPlayers function simply returns the hashmap as an arrayList*/
   @Override
   public Collection<Player> getAllPlayers() {
	   return new ArrayList<Player>(players.values());
   }

   @Override
   public void addGameEngineCallback(GameEngineCallback gameEngineCallback) {
      this.gameEngineCallback = gameEngineCallback;
   }

   @Override
   public void removeGameEngineCallback(GameEngineCallback gameEngineCallback) {
	   this.gameEngineCallback = null;
   }
   
   public Player getMainPlayer(){
      return mainPlayer;
   }

   /* sendUpdatePlayer serialized a packet with updated main player */
   public void sendUpdatePlayer(){
      ObjectOutputStream outputStream = null;
      try {
         outputStream = new ObjectOutputStream(socket.getOutputStream());
         Packet packet = new Packet("updatePlayer", mainPlayer, null, 0);
         outputStream.writeObject(packet);
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   /* handleInput is the mainfunction to handle inputs depending on the packet
    * name. */
   public void handleInput(Packet packet){
      if(packet.getName().equals("updatePlayers")){
         ArrayList<Player> players = packet.getPlayers();
         if(players != null){
            for(int i = 0; i < players.size(); i++){
               if(this.players.containsKey(players.get(i).getPlayerId())){
                  Player updatingPlayer = this.players.get(players.get(i).getPlayerId());
                  updatingPlayer.placeBet(players.get(i).getNumber(), players.get(i).getBet());
                  updatingPlayer.setPoints(players.get(i).getPoints());
               } 
               else{
                  addMultiPlayer(players.get(i));
               }
            }
         }
      }
      if(packet.getName().equals("spin")){         
         gameEngineCallback = new ClientGameEngineCallbackServer(controller.getWindow());
      }
      if(packet.getName().equals("spinNumberNext")){
         if(gameEngineCallback != null){
            int num = packet.getNumber();
            gameEngineCallback.nextNumber(num, this);
         }
      }
      if(packet.getName().equals("result")){
         int result = packet.getNumber();
         gameEngineCallback.result(result, this);
         controller.getWindow().openResultDialog();
         calculateResult(result);
         requestRefresh();
      }
   }

   /* requestRefresh serialize a packet and request for a refresh */
   public void requestRefresh(){
      ObjectOutputStream outStream;

      try
      {
         outStream = new ObjectOutputStream(socket.getOutputStream());
         Packet packet = new Packet("requestRefresh", null, null, 0);
         outStream.writeObject(packet);
      }
      catch (IOException  e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   /* requestSpin serialize a packet and request for a spin */
   public void requestSpin(){
      ObjectOutputStream outStream;
      try
      {
         outStream = new ObjectOutputStream(socket.getOutputStream());
         Packet packet = new Packet("spin", null, null, 0);
         outStream.writeObject(packet);
      }
      catch (IOException  e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   @Override
   public void run()
   {
      
      while(socket.isConnected()){
         ObjectInputStream inStream;
         try
         {
            inStream = new ObjectInputStream(socket.getInputStream());

            Object o = inStream.readObject();
            if( o instanceof Packet){
               handleInput((Packet) o);
            }
         }
         catch (IOException | ClassNotFoundException e)
         {
            e.printStackTrace();
         }
      }      
   }
}