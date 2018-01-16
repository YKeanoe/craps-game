package model.interfaces;

/**
 * Assignment interface for SADI to notify client of GameEngine events e.g.
 * wheel spinning
 * 
 * @author Caspar Ryan
 * 
 */
public interface GameEngineCallback
{
	/**
	 * called when the next number on the wheel comes up use this to update your
	 * display
	 * 
	 * @param nextNumber
	 *            the current number as the wheel spins
	 * @param engine
	 *            a reference to the engine so the receiver can call methods as
	 *            necessary
	 * @see model.interfaces.GameEngine
	 */
	public void nextNumber(int nextNumber, GameEngine engine);

	/**
	 * 
	 * called when wheel has stopped, process bets here
	 * 
	 * @param result
	 *            the number the wheel stopped on
	 * @param engine
	 *            a reference to the engine so the receiver can call methods as
	 *            necessary
	 */
	public void result(int result, GameEngine engine);
}
