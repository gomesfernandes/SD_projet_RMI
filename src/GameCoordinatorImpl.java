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

public class GameCoordinatorImpl 
		extends UnicastRemoteObject 
		implements GameCoordinator 
{
	private boolean gameOngoing = false;
	private ArrayList<Agent> competitors = new ArrayList<Agent>();
	private Map<Agent,Integer> producers = new HashMap<Agent,Integer>();
	
	public GameCoordinatorImpl() throws RemoteException {}
	
	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */ 
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
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
		
	}
}
