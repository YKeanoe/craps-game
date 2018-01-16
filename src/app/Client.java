package app;

import java.io.IOException;
import java.net.Socket;
import controller.CasinoWheelController;

public class Client {
	public static void main(String args[])
	{
      Socket socket;
      String serverHostname = new String ("127.0.0.1");
      try {
         socket = new Socket(serverHostname, 10013);
         new CasinoWheelController(socket);  
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
	}
}
