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
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void playerFinished() throws RemoteException;
}
