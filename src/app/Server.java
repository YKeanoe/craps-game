package app;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.GameEngineImpl;
import model.GameEngineServerStub;
import model.interfaces.GameEngine;

public class Server {
   static Server server;
   static ArrayList<GameEngineServerStub> threads = new ArrayList<GameEngineServerStub>();
   static ArrayList<Socket> sockets = new ArrayList<Socket>();

   public static void main(String args[]) throws IOException
   {
      ServerSocket ssock = new ServerSocket(10013);
      Socket socket;
      GameEngine gameEngine = new GameEngineImpl();
      server = new Server();
      
       while (true)
       {
          socket = ssock.accept();
          threads.add(new GameEngineServerStub(socket, gameEngine, server));
          new Thread(threads.get(threads.size()-1)).start();
       }
   }
   
   public ArrayList<GameEngineServerStub> getAllthreads(){
      return threads;
   }
   
}
