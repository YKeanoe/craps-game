package view;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.*;
import model.interfaces.GameEngine;
import model.interfaces.Player;

/**
 * Simple console client for SADI assignment 1, Semester 1, 2016
 * 
 * @author Caspar Ryan
 * 
 */
public class Client
{
	private static Logger logger = Logger.getLogger("assignment1");
	private static final int WHEEL_SIZE = 40;
	private static final int BET = 100;

	public static void main(String args[])
	{
		final GameEngine gameEngine = new GameEngineImpl();
		
		// create two test players
		Player[] players = new Player[]
		{ new SimplePlayer("1", "The Hustler", 1000),
				new SimplePlayer("2", "The Loser", 500) };

		gameEngine.addGameEngineCallback(new GameEngineCallbackImpl(null));

		// main loop to add player place a bet
		for (Player player : players)
		{
			gameEngine.addPlayer(player);
			int number = (int) (Math.random() * WHEEL_SIZE);
			logger.log(Level.INFO, "lucky number=" + number);
			logger.log(Level.INFO, "placeBet returned "
					+ gameEngine.placeBet(player, number, BET));
		}

		// all players have bet so now do spin
		// so that GameEngineCallBacks are called
		int result = gameEngine.spin(WHEEL_SIZE, 1, 300, 30);

		// update player point balances
		gameEngine.calculateResult(result);

		// log final points balances so we can check correctness
		for (Player player : gameEngine.getAllPlayers()){
		   System.out.println(player.getPlayerName());
		   System.out.println(player.getPoints());
			logger.log(Level.INFO, player.toString());
		}
	}
}
