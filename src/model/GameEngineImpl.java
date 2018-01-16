package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import controller.CasinoWheelController;
import model.interfaces.GameEngine;
import model.interfaces.GameEngineCallback;
import model.interfaces.Player;

public class GameEngineImpl implements GameEngine
{
   GameEngineCallback gameEngineCallback;
   CasinoWheelController controller;
   HashMap<String, Player> players = new HashMap<>();
   
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

   /* addPlayer function adds player to the hashmap. */
   @Override
   public void addPlayer(Player player) {
      players.put(player.getPlayerId(), player);
   }

   /* removePlayer function checks if there is player's hashmap, then remove 
    * player. */
   @Override
   public boolean removePlayer(Player player)
   {
      if(players.size() == 0)
         return false;
      
	   if(players.get(player.getPlayerId()) == null)
		   return false;
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
      return true;
   }

   /* calculateResult function checks every players if they had bet.
    * For every players that bet, if they win, it will add their points
    * double their bet. If they lose, it will simply decrease their points. */
   @Override
   public void calculateResult(int result) {
	   for(Player player : players.values()) {
		   if(player.getBet() > 0){
			   int points = player.getPoints();
			   int bet = player.getBet();
			   int pool;
		
			   if(player.getNumber() == result)
				   pool = points + (bet * 2);
			   else
				   pool = points - bet;
		
			   player.setPoints(pool);
		   }
		   if(player.getPoints() <= 0)
			   removePlayer(player);
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
}