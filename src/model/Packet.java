package model;

import java.io.Serializable;
import java.util.ArrayList;

import model.interfaces.Player;

public class Packet implements Serializable {
   String name;
   Player player;
   ArrayList<Player> players;
   int number;
   
   public Packet(String name, Player player, ArrayList<Player> players, int num){
      this.name = name;
      this.player = player;
      this.players = players;
      this.number = num;
   }
   public String getName(){
      return name;
   }
   public Player getPlayer(){
      return player;
   }  
   public ArrayList<Player> getPlayers(){
      return players;
   }
   public int getNumber(){
      return number;
   }
}
