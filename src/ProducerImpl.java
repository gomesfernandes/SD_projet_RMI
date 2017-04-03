import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.net.InetAddress;
import java.net.MalformedURLException ;
import java.net.UnknownHostException;
//import Producer;

public class ProducerImpl 
		extends UnicastRemoteObject 
		implements Producer 
{
	private int nbCopies = 50;
	private boolean productionOngoing = false;
	private int ressourceType;
	
	/**
	 * @brief Producer constructor 
	 * @param an integer representing the type of ressource to produce
	 */ 
	public ProducerImpl(int rtype) throws RemoteException {
			ressourceType = rtype;
	}
	
	/**
	 * @brief Produces 5 new nbCopies of the ressource.  
	 */ 
	public synchronized void produce() throws RemoteException {
		nbCopies+=5;
		System.out.println("new copies of ressource");
	}
	
	/**
	 * @brief Allows a player to ask for n nbCopies of the ressource.  
	 * If there are less nbCopies than n, the remaining number of nbCopies
	 * is returned. If n < 0, nothing is returned.
	 * @param number of nbCopies desired
	 * @return number of nbCopies actually returned 
	 */ 
	public synchronized int takeCopies(int n) throws RemoteException {
		if (n <= 0) {
			return 0;
		} else if (n <= nbCopies) {
			nbCopies -= n;
			return n;
		} else {
			int tmp = nbCopies;
			nbCopies = 0;
			return tmp;
		}
	}
	
	/**
	 * @brief Launches the production.
	 */ 
	public void startProduction() throws RemoteException {
		productionOngoing = true;
		System.out.println("Production of ressource started");
	}

	public static void main(String args[]) {
		
		if (args.length != 4) {
			System.out.println("Usage : java ProducerImpl <ressource type>" 
						+" <port> <GameCoord Host> <GameCoord Port>") ;
			System.exit(0) ;
		}
		
		String bindname = "rmi://localhost:" + args[1] + "/Producer";
		
		try {
			int port = Integer.parseInt(args[0]);
			String hostIP = InetAddress.getLocalHost().getHostAddress();
			
			/* create local producer object and bind it */
			ProducerImpl p = new ProducerImpl(port);
			Naming.bind(bindname ,p);
			
			/* access game coordinator and notify him about us */
			GameCoordinator gameCoord = (GameCoordinator) Naming.lookup(
				"rmi://" + args[2] + ":" + args[3] + "/GameCoordinator");
			gameCoord.addProducer(hostIP,args[1],port);
			
			System.out.println("Producer running, waiting for game");

		} 
		catch (NumberFormatException e) { 
			System.out.println("Not a ressource"); 
			System.out.println(e) ; 
		}
		catch (AlreadyBoundException e) {
			System.out.println("Is this port already used by another "+
					"producer on this host machine?"); 
			System.out.println(e) ;
		}
		catch (RemoteException e) { System.out.println(e) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (UnknownHostException re) { System.out.println(re) ; } 

	}
}
