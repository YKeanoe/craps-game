package model;

import java.io.Serializable;

import model.interfaces.Player;

public class SimplePlayer implements Player, Serializable
{
   String playerID;
   String playerName; 
   int points;
   int bet;
   int betNumber;
   
   public SimplePlayer(String playerID, String playerName, int point){
      this.playerID = playerID;
      this.playerName = playerName;
      this.points = point;
   }

   @Override
   public String getPlayerName() {
      return playerName;
   }

   @Override
   public void setPlayerName(String playerName) {
      this.playerName = playerName;
   }

   @Override
   public int getPoints() {
      return points;
   }

   @Override
   public void setPoints(int points) {
      this.points = points;
   }

   @Override
   public String getPlayerId() {
	   return playerID;
   }

   @Override
   public boolean placeBet(int number, int bet) {
      if(points >= bet){
         this.betNumber = number;
         this.bet = bet;
         return false;
      }
      else
         return true;
   }

   @Override
   public int getBet()
   {
      return bet;
   }

   @Override
   public void resetBet() {
      this.bet = 0;
      this.betNumber = 0;
   }

   @Override
   public int getNumber()
   {
      return betNumber;
   }

   @Override
   public String toString()
   {
      String toString = "Player " + this.playerID + ", " + playerName + ". " + points + " points.";
      return toString;
   }
}
