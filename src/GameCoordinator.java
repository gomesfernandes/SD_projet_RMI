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
 * This remote interface contains the main methods that are needed to 
 * set up the game. All producers and players must use it to declare
 * their presence. In return, it returns information about the agents
 * of the game, such as their addresses.
 */ 
public interface GameCoordinator extends Remote {
	
	/**
	 * Signals to the coordinator that one of the players is a human.
	 * As a consequence, all players must take turns.
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void hasHumanPlayer() throws RemoteException;
	
	/**
	 * @return true if one of the players is a human, false otherwise
	 * @exception RemoteException exception occurred during remote call
	 */
	public boolean isHumanPresent() throws RemoteException;
	
	/**
	 * Adds a new producer to the corresponding list of agents. The type
	 * of resource it produces is added to a list unless it already 
	 * contains this resource.
	 * @param host		the producer's host address
	 * @param port		the producer's port address
	 * @param r			the type of resource it produces
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void addProducer(String host,String port, Integer r) 
												throws RemoteException;
	
	/**
	 * Adds a new player to the corresponding list of agents.
	 * @param host		the producer's host address
	 * @param port		the producer's port address
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void addPlayer(String host,String port) throws RemoteException;
	
	/**
	 * @return the number of players in the game
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public int getNumberPlayers() throws RemoteException;
	
	/**
	 * @return the number of producers in the game
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public int getNumberProducers() throws RemoteException;
	
	/**
	 * @return the list of players in the game (as a list of Agents)
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public ArrayList<Agent> getPlayers() throws RemoteException;
	
	/**
	 * @return the HashMap of producers and their respective resources.
	 * 						The producers are represented as Agents.
	 * @exception RemoteException exception occurred during remote call
	 */
	public Map<Agent,Integer> getProducers() throws RemoteException;
	
	/**
	 * @param duration	the time it took to finish one round
	 * @exception RemoteException exception occurred during remote call
	 */
	public void addRoundTime(long duration) throws RemoteException;
}
