package model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.Server;
import model.interfaces.GameEngine;
import model.interfaces.Player;

public class GameEngineServerStub implements Runnable
{
   
   Socket socket;
   GameEngine gameEngine;
   Server server;
   public GameEngineServerStub(Socket serverSocket, GameEngine gameEngine, Server server) throws IOException{
      this.socket = serverSocket;
      this.gameEngine = gameEngine;
      this.server = server;
   }
   
   @Override
   public void run()
   {
      try
      {
            while(socket.isConnected()){
               refresh();
               ObjectInputStream inStream = null;
               inStream = new ObjectInputStream(socket.getInputStream());
               Object o = inStream.readObject();
               if( o instanceof Packet){
                  handlePacket((Packet) o);
               }
            }
         
      }
      catch (IOException | ClassNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }      
   }
   
   public void handlePacket(Packet packet){
      if(packet.getName().equals("updatePlayer")){
         Player player = packet.getPlayer();
         ArrayList<Player> players = new ArrayList<Player>(gameEngine.getAllPlayers());
         if(player != null){
            if(players.size() == 0){
               gameEngine.addPlayer(player);
            }
            else{
               boolean update = false;
               for(int i = 0; i<players.size(); i++){
                  if(players.get(i).getPlayerId().equals(player.getPlayerId())){
                     update = true;
                     players.get(i).placeBet(player.getNumber(), player.getBet());
                  }
               }
               if(!update){
                  gameEngine.addPlayer(player);
               }
            }
         }
         refresh();
      }
      if(packet.getName().equals("requestRefresh")){
         refresh();
      }
      if(packet.getName().equals("spin")){
         ArrayList<GameEngineServerStub> threads = server.getAllthreads();
         for(int i = 0; i<threads.size(); i++){
            threads.get(i).sendSpin();
            threads.get(i).gameEngine.addGameEngineCallback(new ServerStubGameEngineCallback(threads.get(i).getSocket()));
            gameEngine.spin(40,  1, 300, 10);
         }
         refresh();
      }
   }

   public ArrayList<GameEngineServerStub> getAllThreads(){
      return server.getAllthreads();
   }
   
   public Socket getSocket(){
      return socket;
   }
   
   public GameEngine getGameEngine(){
      return gameEngine;
   }
   
   public void sendSpin(){
      // Refresh
      ObjectOutputStream outputStream = null;
      try {
         outputStream = new ObjectOutputStream(socket.getOutputStream());
         Packet packetS = new Packet("spin", null, null, 0);
         outputStream.writeObject(packetS);     
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   
   
   public void refresh(){
      
      // Refresh
      ObjectOutputStream outputStream = null;
      try {
         ArrayList<Player> players = new ArrayList<Player>(gameEngine.getAllPlayers());
         if(players.size() == 0){
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Packet packetS = new Packet("empty", null, null, 0);
            outputStream.writeObject(packetS);
         } 
         else{
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Packet packetS = new Packet("updatePlayers", null, players, 0);
            outputStream.writeObject(packetS);
         }
         
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
}
