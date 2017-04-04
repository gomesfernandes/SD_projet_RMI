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
	private ArrayList<Ressource> ressources;
	private boolean myTurn;
	private String host;
	private String port;
	
	public PlayerImpl(String h, String p) throws RemoteException {
		host = h;
		port = p;
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
		ressources = new ArrayList<Ressource>();
		
		Iterator<Agent> producerIter = p.keySet().iterator();
		while (producerIter.hasNext()) {
			Agent a = producerIter.next();
			try {
				Producer prod = (Producer) Naming.lookup("rmi://"+ 
							a.getHost()+ ":" + a.getPort() + "/Producer");
				producers.add(prod);
				
				Ressource r = new Ressource(p.get(a));
				if (!ressources.contains(r)) {
					ressources.add(r);
				} else {
					r = ressources.get(ressources.indexOf(r));
				}
				r.addProducer(prod);
			} catch (NotBoundException re) { 
				System.err.println("Cannot find producer on host "+
								a.getHost()+" and port "+a.getPort()) ; 
				System.exit(1);
			} catch (MalformedURLException e) { System.err.println(e);}
		}
	}
	
	/**
	 * @brief Indicate that it's the players' turn.
	 */ 
	public void setMyTurn(boolean b) throws RemoteException {
		myTurn = b;
	}
	
	//public int steal(Player p) throws RemoteException {}
	//public int getRessource(int type, int n) throws RemoteException {
	//	producers[type].takeCopies(n);
	//}

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
			
		} 
		catch (RemoteException re) { System.err.println(re) ; }
		catch (AlreadyBoundException e) { System.err.println(e) ; }
		catch (MalformedURLException e) { System.err.println(e) ; }
		catch (NotBoundException re) { System.err.println(re) ; }
		catch (UnknownHostException re) { System.err.println(re) ; }
		
	}
}
