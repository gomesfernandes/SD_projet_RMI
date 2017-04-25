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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner; 

/**
 * This class is the implementation of the remote interface Player. 
 * A player's goal is to get resources from one or more producers until
 * the number of copies per resources fulfils an objective.
 * @see Player
 */ 
public class PlayerImpl 
		extends UnicastRemoteObject 
		implements Player 
{
	private int objective;
	private int personalityType;
	private boolean observingAllowed = false; 
	private ArrayList<Player> competitors;
	private ArrayList<Producer> producers;
	private ArrayList<Resource> resources;
	private boolean roundStarted = false;
	private boolean myTurn = false;
	private String host;
	private String port;
	private int nextRess = 0;
	private RoundCoordinator roundCoord = null;
	
	/**
	 * @param h		the host address of the player's machine
	 * @param p		the player's port
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public PlayerImpl(String h, String p) throws RemoteException {
		host = h;
		port = p;
	}
	
	/**
	 * @return true if it's the player's turn, false if not
	 */ 
	public boolean isMyTurn() { return myTurn; }
	
	/** {@inheritDoc} */ 
	public synchronized void setMyTurn(boolean b) throws RemoteException {
		myTurn = b;
		notify();
	}
	
	/** @param type the personality Type */
	public void setPeronalityType(char type) { personalityType = type; }
	
	/**
	 * As long as the objective is not reached for each resource, one
	 * must keep playing.
	 * @return true if the objective is yet reached, false if not
	 */ 
	public boolean isObjectiveReached() {
		if (resources == null) {
			return false;
		}
		for (int i=0; i<resources.size(); i++) {
			if (resources.get(i).getLeftToFind() > 0) {
				return false;
			}
		}
		return true;
	}
	
	/** {@inheritDoc} */ 
	public void setObjective(Map<Integer,Integer> o) throws RemoteException {
		for (Resource r: resources) {
			r.setObjective(o.get(r.getType()));
		}
	}
	
	/** {@inheritDoc} */ 
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
	
	/** {@inheritDoc} */ 
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
	
	/** {@inheritDoc} */ 
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
	
	/** {@inheritDoc} */ 
	public void setObserving(boolean b) throws RemoteException {
		observingAllowed = b;
	}
	
	/** @return the coordinator of the round
	 */ 
	public RoundCoordinator getRoundCoordinator() {
		return roundCoord;
	}
	
	/**
	 * Takes as many copies as necessary to fullfil our objective. If the
	 * player is a human, they will be asked for input.
	 * @param human		true if the player is a human
	 * @exception RemoteException exception occurred during remote call
	 */ 
	public void makeMove(boolean human) throws RemoteException {
		Producer p;
		Resource r;
		int copies = 0;
		
		if (human) {
			int chosenType;
			System.out.println("Choose a resource:");
			for (Resource res : resources) {
				System.out.println("R"+res.getType()+" :"
								+res.getLeftToFind()+" left to find");
			}
			Scanner reader = new Scanner(System.in);
			do {
				System.out.println("Enter type number:");
				chosenType = reader.nextInt();
				reader.nextLine();
				r = new Resource(chosenType);
			} while (!resources.contains(r));
			r = resources.get(resources.indexOf(r));
		} else {
			// cycle through resources that are not yet complete
			do {
				r = resources.get(nextRess);
				nextRess = (nextRess+1)%resources.size();
			} while (r.getLeftToFind() == 0 && !isObjectiveReached());
		}	
		
		List<Producer> prods = r.getProducers();
		if (observingAllowed) { //choose producer with the most copies
			int max = 0, nbRess = prods.get(0).getNbCopies();
			for (int i=1;i<prods.size();i++) {
				if (prods.get(i).getNbCopies() > nbRess) {
					max = i;
					nbRess = prods.get(i).getNbCopies();
				}
			}
			p = prods.get(max);
		} else { //choose a random producer
			int i = ThreadLocalRandom.current().nextInt(0, prods.size());
			p = prods.get(i);
		}

		copies = p.takeCopies(r.getLeftToFind());
		if (copies != 0) {
			r.addCopies(copies);
			System.out.println("Took "+copies+" of R"+p.getResourceType());
		}

		myTurn = false;
		if (isObjectiveReached()) roundCoord.playerFinished();
		roundCoord.turnFinished();
	}
	
	public synchronized void waitForTurn() {
		while(!isMyTurn()) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	public synchronized void waitForRound() {
		try {
			while(!roundCoord.isRoundOngoing()) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
		}
		catch (RemoteException re) { System.out.println(re) ; }
	}

	/**
	 * Launches a player that connects to the GameCoordinator, then waits
	 * for his turn to play. When he finishes a round, he contacts the
	 * coordinator of the round.
	 * @param args		the command line options
	 */ 
	public static void main(String args[]) {
		boolean isHuman = false;
		char personalityType = Player.COOPERATIVE;
		
		if (args.length < 3 || args.length > 5) {
			System.out.println("Usage : java PlayerImpl <port>" +
						"<GameCoord Host> <GameCoord Port> [h] [i] ") ;
			System.exit(0) ;
		} else if (args.length == 4) { 
			if ("h".compareTo(args[3].toLowerCase()) == 0) {
				isHuman = true;
				System.out.println("This player is a human.");
			} else if  ("i".compareTo(args[3].toLowerCase()) == 0) {
				personalityType = Player.INDIVIDUALIST;
			}
		} else if (args.length == 5) {
			if (("h".compareTo(args[3].toLowerCase()) == 0)
				|| ("h".compareTo(args[4].toLowerCase()) == 0) ) {
				isHuman = true;
				System.out.println("This player is a human.");
			} else if  (("i".compareTo(args[3].toLowerCase()) == 0)
				|| ("i".compareTo(args[4].toLowerCase()) == 0) ) {
				personalityType = Player.INDIVIDUALIST;
			}
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
			if (isHuman) gameCoord.hasHumanPlayer();
			
			System.out.println("Player running, waiting for game");
			/* wait for RoundCoordinator */
			do {
				Thread.sleep(500);
			} while (j.getRoundCoordinator() == null);
			RoundCoordinator coord = j.getRoundCoordinator();
			
			/* wait for round to start */
			do {
				Thread.sleep(500);
			} while (!coord.isRoundOngoing());
			
			System.out.println("Round started");

			if (coord.isTurnsSet()) {
				/* always wait for my turn */
				do {
					j.waitForTurn();
					if (isHuman){
						j.makeMove(true);
					} else {
						j.makeMove(false);
					}
				} while (!j.isObjectiveReached());
			} else {
				/* grab ressources as soon as possible but wait a bit */
				do {
					j.makeMove(false);
					Thread.sleep(500);
				} while (!j.isObjectiveReached());
			}
			
			System.out.println("Round finished!");
		} 
		catch (RemoteException re) {System.err.println(re);System.exit(1); }
		catch (AlreadyBoundException e) {System.err.println(e);System.exit(1); }
		catch (MalformedURLException e) {System.err.println(e); System.exit(1);}
		catch (NotBoundException re) {System.err.println(re);System.exit(1);}
		catch (UnknownHostException re) {System.err.println(re);System.exit(1);}
		catch (Exception e) {}
	}
}
