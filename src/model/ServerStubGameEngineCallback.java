package model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import app.Server;
import model.interfaces.GameEngine;
import model.interfaces.GameEngineCallback;
import view.GameWindow;

public class ServerStubGameEngineCallback extends Thread implements GameEngineCallback, Runnable {
	GameWindow gameWindow;	
	Socket socket;
	/* nextNumber function will get the nextNumber in the spin iteration and
	 * change the numberLabel in the GUI to nextNumber. */
	@Override
   public void nextNumber(final int nextNumber, GameEngine engine)
   {
	   
      ObjectOutputStream outputStream = null;
      try {
         outputStream = new ObjectOutputStream(socket.getOutputStream());
         Packet packetS = new Packet("spinNumberNext", null, null, nextNumber);
         outputStream.writeObject(packetS);     
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      System.out.println();
   }

	/* result function will get the result from the last result of spin function
	 * and change the numberLabel in the GUI to result. */
	@Override
	public void result(int result, GameEngine engine) {
	   ObjectOutputStream outputStream = null;
	   try {
	      outputStream = new ObjectOutputStream(socket.getOutputStream());
	      Packet packetS = new Packet("result", null, null, result);
	      outputStream.writeObject(packetS);     
	   }
	   catch (IOException e)
	   {
	      e.printStackTrace();
	   }
	}

	public ServerStubGameEngineCallback(Socket socket){
	   this.socket = socket;   
	}
}
