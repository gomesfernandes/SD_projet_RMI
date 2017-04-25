/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * This remote interface allows a player to declare that they have 
 * finished the game round.
 */ 
public interface RoundCoordinator extends Remote {
	/** Used by a player to notify that they have reached their objectives.
	 * @param id the player's ID, as assigned by the RoundCoordinator
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void playerFinished(int id) throws RemoteException;
	
	/** @return true if the round is finished, false if not 
	 * @exception RemoteException exception occurred during remote call
	 * */
	public boolean isRoundOngoing() throws RemoteException;
	
	/** @return true if players take turns, false otherwise 
	 * @exception RemoteException exception occurred during remote call
	 */
	public boolean isTurnsSet() throws RemoteException;
	
	/** Tells the coordinator that the player has made his move.
	 * @exception RemoteException exception occurred during remote call
	 */
	public void turnFinished() throws RemoteException;
}
