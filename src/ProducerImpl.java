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

/**
 * This class is the implementation of the remote interface Producer. 
 * A producer creates a single type of resource. It starts with 50 copies,
 * and then, once production is started by a coordinator, it producers 
 * 5 more copies each second.
 * @see Producer
 */ 
public class ProducerImpl 
		extends UnicastRemoteObject 
		implements Producer 
{
	private int nbCopies = 50;
	private int maxN;
	private boolean productionOngoing = false;
	private int resourceType;
	
	/**
	 * @param an integer representing the type of resource to produce
	 */ 
	public ProducerImpl(int rtype) throws RemoteException {
			resourceType = rtype;
	}
	
	/** {@inheritDoc} */
	public int getResourceType() throws RemoteException {
		return resourceType;
	}
	
	/** {@inheritDoc} */
	public int getNbCopies() throws RemoteException { return nbCopies;}
	
	/** {@inheritDoc} */
	public synchronized int takeCopies(int n) throws RemoteException {
		int r;
		if (n>maxN) n = maxN;
		if (n <= 0 || nbCopies == 0) {
			r = 0;
		} else if (n <= nbCopies) {
			nbCopies -= n;
			r = n;
			System.out.println(r+" copies taken. "+nbCopies+" left");
		} else {
			int tmp = nbCopies;
			nbCopies = 0;
			r = tmp;
			System.out.println(r+" copies taken. "+nbCopies+" left");
		}
		return r;
	}
	
	/** {@inheritDoc} */ 
	public void setMaxTaken(int n) throws RemoteException { maxN = n;}
	
	/** {@inheritDoc} */
	public void startProduction() throws RemoteException {
		productionOngoing = true;
		System.out.println("Production of R"+resourceType+" started");
	}
	/** {@inheritDoc} */
	public void stopProduction() throws RemoteException {
		productionOngoing = false;
		nbCopies = 50;
		System.out.println("Production of R"+resourceType+" stopped");
	}

	/** 
	 * Produce 5 new copies of the resource.
	 */ 
	public synchronized void produce() throws RemoteException {
		if (productionOngoing) {
			nbCopies+=5;
			//nbCopies = nbCopies + nbCopies/2 + 1;
			System.out.println("5 new copies of R"+resourceType+". total:"+nbCopies);
		}
	}

	/**
	 * Launches a producer that connects to the GameCoordinator, then 
	 * waits for the production go.
	 * @param args		the command line options
	 */ 
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
		catch (RemoteException e) {System.out.println(e);System.exit(1); }
		catch (MalformedURLException e) {System.out.println(e);System.exit(1); }
		catch (NotBoundException re) {System.out.println(re);System.exit(1); }
		catch (UnknownHostException re) {System.out.println(re);System.exit(1); } 
		catch (Exception e) {System.out.println(e);System.exit(1);}
	}
}
