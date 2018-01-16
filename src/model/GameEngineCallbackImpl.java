package model;

import javax.swing.SwingUtilities;

import model.interfaces.GameEngine;
import model.interfaces.GameEngineCallback;
import view.GameWindow;

public class GameEngineCallbackImpl extends Thread implements GameEngineCallback {
	GameWindow gameWindow;	
 
	/* nextNumber function will get the nextNumber in the spin iteration and
	 * change the numberLabel in the GUI to nextNumber. */
	@Override
   public void nextNumber(final int nextNumber, GameEngine engine)
   {
	   if(gameWindow == null)
		   System.out.println(nextNumber);
	   else{
		   SwingUtilities.invokeLater(new Runnable() {
			   @Override
			   public void run() {
				   gameWindow.getNumberLabel().setText(Integer.toString(nextNumber));
			   }
		   });
	   }
   }

	/* result function will get the result from the last result of spin function
	 * and change the numberLabel in the GUI to result. */
   @Override
   public void result(int result, GameEngine engine) {
	   if(gameWindow == null)
		   System.out.println("Result = " + result);
	   else
		   gameWindow.getNumberLabel().setText(Integer.toString(result));
   }

   public GameEngineCallbackImpl(GameWindow gameWindow){
		this.gameWindow = gameWindow;
   }
}
