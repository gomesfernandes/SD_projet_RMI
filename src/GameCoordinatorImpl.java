/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class is the implementation of the remote interface GameCoordinator.
 * To create a new game, this must be the first program to be executed, 
 * as it will keep track of the agents. In fact, to be part of the game, 
 * producers and players must "register" themselves with a remote call. 
 * 
 * @see GameCoordinator
 */ 
public class GameCoordinatorImpl 
		extends UnicastRemoteObject 
		implements GameCoordinator 
{
	private boolean humanPlayer = false;
	private ArrayList<Agent> competitors = new ArrayList<Agent>();
	private Map<Agent,Integer> producers = new HashMap<Agent,Integer>();
	private ArrayList<Long> roundTimes = new ArrayList<Long>();
	
	public GameCoordinatorImpl() throws RemoteException {}
	
	/** {@inheritDoc} */ 
	public void hasHumanPlayer() throws RemoteException {
		System.out.println("Human detected");
		humanPlayer = true;
	}
	
	/** {@inheritDoc} */ 
	public boolean isHumanPresent() throws RemoteException {
		return humanPlayer;
	}
	
	/** {@inheritDoc} */  
	public void addProducer(String host,String port, Integer r) 
											throws RemoteException {
		Agent a = new Agent(host,port,Agent.PRODUCER);
		if (!producers.containsKey(a)) {
			producers.put(a,r);
		}
		System.out.println("Added producer : "+host+","+port+","+r);
	}
	
	/** {@inheritDoc} */ 
	public void addPlayer(String host,String port) throws RemoteException {
		competitors.add(new Agent(host,port,Agent.PLAYER));
		System.out.println("Added player : "+host+","+port);
	}

	/** {@inheritDoc} */ 
	public int getNumberPlayers() throws RemoteException {
		return competitors.size();
	}
	
	/** {@inheritDoc} */ 
	public int getNumberProducers() throws RemoteException {
		return producers.size();
	}
	
	/** {@inheritDoc} */ 
	public ArrayList<Agent> getPlayers() throws RemoteException {
		return competitors;
	}
	
	/** {@inheritDoc} */ 
	public Map<Agent,Integer> getProducers() throws RemoteException {
		return producers;
	}
	
	/** {@inheritDoc} */ 
	public void addRoundTime(long duration) throws RemoteException {
		roundTimes.add(duration);
		System.out.println("New average round time: "+getAvgRoundTime());
	}
	
	/** @return the average time it takes to finish a round */
	private long getAvgRoundTime() throws RemoteException {
		if (roundTimes.size() == 0) 
			return 0;
		else {
			Long sum = 0l;
			for (Long l : roundTimes)
				sum+=l;
			return sum/roundTimes.size();
		}
	}
	
	/**
	 * Starts the game by creating a distant object that will be called
	 * by the other agents. The coordinator's rmiregistry port must be 
	 * given in the command line.
	 * @param args		the command line options
	 */ 
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage : java GameCoordinatorImpl <port>") ;
			System.exit(0) ;
		}
		
		String bindname = "rmi://localhost:"+args[0]+"/GameCoordinator";
		try {
			GameCoordinatorImpl gameCoord = new GameCoordinatorImpl();
			Naming.rebind(bindname,gameCoord) ;
			System.out.println("Game Coordinator running");
			
		} 
		catch (RemoteException e) {System.err.println(e); System.exit(1); }
		catch (MalformedURLException e) {System.err.println(e); System.exit(1); }
	}
}
