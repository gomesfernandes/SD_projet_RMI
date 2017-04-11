/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
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
	private int resourceType;
	
	/**
	 * @brief Producer constructor 
	 * @param an integer representing the type of resource to produce
	 */ 
	public ProducerImpl(int rtype) throws RemoteException {
			resourceType = rtype;
	}

	/**
	 * @brief Produces 5 new nbCopies of the resource.  
	 */ 
	public synchronized void produce() throws RemoteException {
		if (productionOngoing) {
			nbCopies+=5;
			System.out.println("5 new copies of R"+resourceType+". total:"+nbCopies);
		}
	}
	
	public int getResourceType() throws RemoteException {
		return resourceType;
	}
	
	/**
	 * @brief Allows a player to ask for n nbCopies of the resource.  
	 * If there are less nbCopies than n, the remaining number of nbCopies
	 * is returned. If n < 0, nothing is returned.
	 * @param number of nbCopies desired
	 * @return number of nbCopies actually returned 
	 */ 
	public synchronized int takeCopies(int n) throws RemoteException {
		int r;
		if (n <= 0) {
			r = 0;
		} else if (n <= nbCopies) {
			nbCopies -= n;
			r = n;
		} else {
			int tmp = nbCopies;
			nbCopies = 0;
			r = tmp;
		}
		System.out.println("copies taken. total:"+nbCopies);
		return r;
	}
	
	/**
	 * @brief Launches or stops the production.
	 */ 
	public void startProduction() throws RemoteException {
		productionOngoing = true;
		System.out.println("Production of R"+resourceType+" started");
	}
	public void stopProduction() throws RemoteException {
		productionOngoing = false;
		System.out.println("Production of R"+resourceType+" stopped");
	}

	public static void main(String args[]) {
		
		if (args.length != 4) {
			System.out.println("Usage : java ProducerImpl <resource type>" 
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
			
			System.out.println("Producer running");
			while (true) {
				p.produce();
				Thread.sleep(1000);
			}
		} 
		catch (NumberFormatException e) { 
			System.out.println("Not a resource"); 
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
		catch (Exception e) {System.out.println(e) ;}
	}
}
