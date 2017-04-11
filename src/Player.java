/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.ArrayList;

/**
 * This remote interface provides all the information that a player 
 * needs in a game, such as where to find his opponents and the producers.
 */ 
public interface Player extends Remote {
	
	public static final char INDIVIDUALIST = 'i';
	public static final char COOPERATIVE = 'c';
	
	/**
	 * Fixes the number of copies the player must obtain for each resource.
	 * @param int		the number of copies to find for all resources
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void setObjective(Map<Integer,Integer> o) throws RemoteException ;
	
	/**
	 * Provides the addresses of all the players in the game.
	 * @param int		a list of Agents (of type players)
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void setPlayers(ArrayList<Agent> j) throws RemoteException;
	
	/**
	 * Provides the addresses of all the producers in the game, and what
	 * they produce.
	 * @param int		a Map of Agents (of type producers) and the type
	 * 							resource they produce
	 * @exception RemoteException exception occurred during remote call
	 */
	public void setProducers(Map<Agent,Integer> p) throws RemoteException;
	
	/**
	 * @param b		true if it's the players turn, false if not
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void setMyTurn(boolean b) throws RemoteException;
	
	/**
	 * @param host		the host address of the RoundCoordinator
	 * @param port		the port of the RoundCoordinator (as a String)
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void setRoundCoordinator(String host, String port) 
												throws RemoteException ;
	
	
	//public int getResource(int type, int n) throws RemoteException;
}
