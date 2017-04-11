/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This remote interface allows the interaction with a producer. In particular,
 * the players must be able to access a Producer's resources.
 */ 
public interface Producer extends Remote {
	/** Allows the producer to create more copies of its resource.
	 * @exception RemoteException exception occurred during remote call
	 */
	public void startProduction() throws RemoteException ;
	
	/** Stops the producer from creating more copies of its resource. 
	 * @exception RemoteException exception occurred during remote call
	 */
	public void stopProduction() throws RemoteException ;
	
	/**
	 * Allows a player to ask for n copies of the resource. If there are 
	 * less than n copies, the remaining number of copies is returned. 
	 * If n < 0, nothing is returned.
	 * @param n		number of copies desired
	 * @return 		number of copies actually returned
	 */ 
	public int takeCopies(int n) throws RemoteException ;
	
	/**
	 * @return the type of resource (as an integer)
	 * @exception RemoteException exception occurred during remote call
	 */
	public int getResourceType() throws RemoteException ;
}
