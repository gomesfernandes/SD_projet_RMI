import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class GameCoordinatorImpl 
		extends UnicastRemoteObject 
		implements GameCoordinator 
{
	private boolean gameOngoing = false;
	private ArrayList<Agent> competitors = new ArrayList<Agent>();
	private Map<Agent,Integer> producers = new HashMap<Agent,Integer>();
	
	public GameCoordinatorImpl() throws RemoteException {}
	
	/**
	 * @brief Marks the end of the game
	 */ 
	public void endGame() throws RemoteException {
		// tell others to quit? 
		System.exit(0) ;
	}	
	
	/**
	 * @brief Adds a new producer to the corresponding list of agents
	 * @param host the host address of the producer
	 * @param port the port address
	 * @param r the type of ressource it produces
	 */ 
	public void addProducer(String host,String port, Integer r) 
											throws RemoteException {
		Agent a = new Agent(host,port,Agent.PRODUCER);
		if (!producers.containsKey(a)) {
			producers.put(a,r);
		}
		System.out.println("Added producer : "+host+","+port+","+r);
	}
	
	/**
	 * @brief Adds a new player to the corresponding list of agents
	 * @param host the host address of the player
	 * @param port the port address
	 */ 
	public void addPlayer(String host,String port) throws RemoteException {
		competitors.add(new Agent(host,port,Agent.PLAYER));
		System.out.println("Added player : "+host+","+port);
	}
	
	/**
	 * @brief Mark the start of the game.
	 */ 
	public void setGameOngoing() throws RemoteException {
		gameOngoing = true;
	}
	
	public int getNumberPlayers() throws RemoteException {
		return competitors.size();
	}
	public int getNumberProducers() throws RemoteException {
		return producers.size();
	}
	public ArrayList<Agent> getPlayers() throws RemoteException {
		return competitors;
	}
	public Map<Agent,Integer> getProducers() throws RemoteException {
		return producers;
	}
	
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage : java GameCoordinatorImpl <port>") ;
			System.exit(0) ;
		}
		
		String bindname = "rmi://localhost:"+args[0]+"/GameCoordinator";
		
		try {
			GameCoordinatorImpl gameCoord = new GameCoordinatorImpl();
			Naming.bind(bindname,gameCoord) ;
			System.out.println("Game Coordinator running");
			
		} 
		catch (RemoteException re) { System.out.println(re) ; }
		catch (AlreadyBoundException e) { System.out.println(e) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
		
	}
}
