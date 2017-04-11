/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.net.* ;
import java.rmi.* ;
import java.net.MalformedURLException ;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
//import Player ;

public class PlayerImpl 
		extends UnicastRemoteObject 
		implements Player 
{
	private int objective;
	private ArrayList<Player> competitors;
	private ArrayList<Producer> producers;
	private ArrayList<Resource> resources;
	private boolean myTurn = false;
	private String host;
	private String port;
	private int nextProd = 0;
	private RoundCoordinator roundCoord;
	
	public PlayerImpl(String h, String p) throws RemoteException {
		host = h;
		port = p;
	}
	
	public boolean isMyTurn() { return myTurn; }
	
	public boolean isObjectiveNotReached() {
		if (resources == null) return true;
		for (int i=0; i<resources.size(); i++) {
			if (resources.get(i).getNbCopies() < objective)
				return true;
		}
		return false;
	}
	
	public void setObjective(int o) throws RemoteException {
		objective = o;
	}
	
	public void setPlayers(ArrayList<Agent> j)throws RemoteException {
		competitors = new ArrayList<Player>();
		Iterator<Agent> playerIter = j.iterator();
		while (playerIter.hasNext()) {
			Agent a = playerIter.next();
			try { 
				Player p = (Player) Naming.lookup("rmi://"+ a.getHost()+ 
									":" + a.getPort() + "/Player");
				if ( !(a.getHost() == host && a.getPort() == port)) {
					competitors.add(p);
				}
			} catch (NotBoundException re) { 
				System.err.println("Cannot find player on host "+
								a.getHost()+" and port "+a.getPort()) ; 
				System.exit(1);
			} catch (MalformedURLException e) { System.err.println(e);}
		}
	}
	
	public void setProducers(Map<Agent,Integer> p) throws RemoteException {
		producers = new ArrayList<Producer>();
		resources = new ArrayList<Resource>();
		
		Iterator<Agent> producerIter = p.keySet().iterator();
		while (producerIter.hasNext()) {
			Agent a = producerIter.next();
			try {
				Producer prod = (Producer) Naming.lookup("rmi://"+ 
							a.getHost()+ ":" + a.getPort() + "/Producer");
				producers.add(prod);
				
				Resource r = new Resource(p.get(a));
				if (!resources.contains(r)) {
					resources.add(r);
				} else {
					r = resources.get(resources.indexOf(r));
				}
				r.addProducer(prod);
			} catch (NotBoundException re) { 
				System.err.println("Cannot find producer on host "+
								a.getHost()+" and port "+a.getPort()) ; 
				System.exit(1);
			} catch (MalformedURLException e) { System.err.println(e);}
		}
	}
	
	
	public void setRoundCoordinator(String host, String port) 
											throws RemoteException {
		try
		{
			roundCoord = (RoundCoordinator) Naming.lookup(
			"rmi://" + host + ":" + port + "/RoundCoordinator") ;
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
	
	public RoundCoordinator getRoundCoordinator() {
		return roundCoord;
	}
	
	/**
	 * @brief Indicate that it's the players' turn.
	 */ 
	public void setMyTurn(boolean b) throws RemoteException {
		myTurn = b;
	}
	
	//public int steal(Player p) throws RemoteException {}
	//public int getResource(int type, int n) throws RemoteException {
	//	producers[type].takeCopies(n);
	//}
	
	public void makeMove() throws RemoteException {
		int copies = 0;
		Producer p = producers.get(nextProd);
		copies = p.takeCopies(objective);
		int i = resources.indexOf(new Resource(p.getResourceType()));
		resources.get(i).addCopies(copies);
		nextProd = (nextProd+1)%producers.size();
		System.out.println("Took "+copies+" of resource "+p.getResourceType());
		myTurn = false;
	}

	public static void main(String args[]) {
		if (args.length != 3) {
			System.out.println("Usage : java PlayerImpl <port>" +
								"<GameCoord Host> <GameCoord Port>") ;
			System.exit(0) ;
		}
		
		String bindname = "rmi://localhost:" + args[0] + "/Player";
		
		try {
			int port = Integer.parseInt(args[0]);
			String hostIP = InetAddress.getLocalHost().getHostAddress();
			
			/* create local producer object and bind it */
			PlayerImpl j = new PlayerImpl(hostIP,args[0]);
			Naming.bind(bindname,j);
			
			/* access game coordinator and notify him about us */
			GameCoordinator gameCoord = (GameCoordinator) Naming.lookup(
				"rmi://" + args[1] + ":" + args[2] + "/GameCoordinator");
			gameCoord.addPlayer(hostIP,args[0]);
			
			System.out.println("Player running");
			do {
				if (j.isMyTurn()) {
					j.makeMove();
				}
				Thread.sleep(1000);
			} while (j.isObjectiveNotReached());
			
			RoundCoordinator coord = j.getRoundCoordinator();
			coord.playerFinished();
			System.out.println("Round finished!");
		} 
		catch (RemoteException re) { System.err.println(re) ; }
		catch (AlreadyBoundException e) { System.err.println(e) ; }
		catch (MalformedURLException e) { System.err.println(e) ; }
		catch (NotBoundException re) { System.err.println(re) ; }
		catch (UnknownHostException re) { System.err.println(re) ; }
		catch (Exception e) {}
	}
}
