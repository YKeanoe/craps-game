package model.interfaces;

import java.util.Collection;

/**
 * Assignment interface for SADI
 * 
 * @author Caspar Ryan
 * 
 */
public interface GameEngine
{
	/**
	 * spin the gaming wheel progressing from the initialDelay to the finalDelay
	 * in increments of delayIncrement delays are in milliseconds (ms)
	 * 
	 * 1. begin by selecting a random starting number on the wheel (between 1
	 * and wheelSize) 2. start at initialDelay then increment the delay each
	 * time a new number is passed on the wheel 3. call
	 * GameEngineCallback.nextNumber(...) each time a number is passed continue
	 * until delay >= finalDelay 4. call GameEngineCallback.result(...) to
	 * finish and process result
	 * 
	 * @param wheelSize
	 *            the size of the wheel from 1 .. wheelSize
	 * @param initialDelay
	 *            the starting delay in ms between numbers when the wheel starts
	 * @param finalDelay
	 *            the final delay in ms between numbers when the wheel stops
	 *            (usually 0)
	 * @param delayIncrement
	 *            how much the wheel slows down after each number is passed
	 * 
	 * @return the final result of the spin (the winning number)
	 */
	public abstract int spin(int wheelSize, int initialDelay, int finalDelay,
			int delayIncrement);

	/**
	 * @param player
	 *            to add to game
	 */
	public abstract void addPlayer(Player player);

	/**
	 * @param player
	 *            to remove from game
	 * @return true if the player existed
	 */
	public abstract boolean removePlayer(Player player);

	/**
	 * @param id
	 *            id of player to retrieve (null if not found)
	 */
	public abstract Player getPlayer(String id);

	/**
	 * Player places a bet for a number on the wheel
	 * 
	 * @param player
	 * @see model.interfaces.Player
	 * @param number
	 *            the lucky number the player picked
	 * @param bet
	 *            how many points to bet
	 * @return true if the bet is placed false if the player does not have
	 *         enough points
	 */
	public abstract boolean placeBet(Player player, int number, int bet);

	/**
	 * engine goes through all players and applies win or loss to points
	 * 
	 * @param result
	 *            the winning number from the wheel
	 */
	public abstract void calculateResult(int result);

	/**
	 * 
	 * @return an unmodifiable collection of all Players
	 * @see model.interfaces.Player
	 */
	public abstract Collection<Player> getAllPlayers();

	/**
	 * @param gameEngineCallback
	 *            a client specific implementation of GameEngineCallback used to
	 *            perform display updates etc.
	 * 
	 *            you will use a different implementation of the
	 *            GameEngineCallback for GUI and console versions
	 * 
	 */
	public abstract void addGameEngineCallback(
			GameEngineCallback gameEngineCallback);

	/**
	 * @param gameEngineCallback
	 *            a client specific implementation of GameEngineCallback used to
	 *            perform display updates etc.
	 * 
	 *            you will use a different implementation of the
	 *            GameEngineCallback for GUI and console versions
	 * 
	 */
	public abstract void removeGameEngineCallback(
			GameEngineCallback gameEngineCallback);
}