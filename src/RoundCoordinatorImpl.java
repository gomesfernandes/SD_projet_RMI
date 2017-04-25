/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.net.MalformedURLException ;
import java.util.Scanner; 
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

public class RoundCoordinatorImpl 
		extends UnicastRemoteObject 
		implements RoundCoordinator 
{
	
	private boolean roundOngoing = false;
	private boolean hasTurns = true;
	public boolean waitingForMove = false;
	private boolean observingAllowed = false;
	private int endType;
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Producer> producers = new ArrayList<Producer>();
	ArrayList<Integer> finishedPlayers = new ArrayList<Integer>();
	private int nbFinishedPlayers = 0;
	private int currentPlayer = -1;
	private int winner = -1;
	
	public RoundCoordinatorImpl() throws RemoteException {}
	
	/** @param b  true if the players should take turns, false otherwise */ 
	public void setTurns(boolean b) { 
		hasTurns = b; 
		if (b) System.out.println("Players will take turns");
		else System.out.println("Players do not take turns");
	}
	
	/** {@inheritDoc} */
	public synchronized boolean isTurnsSet() { return hasTurns; }

	/** Mark the start of the actual game play */
	public synchronized void setRoundOngoing() {
		roundOngoing = true;
		notify();
	}

	/** {@inheritDoc} */
	public synchronized boolean isRoundOngoing() {return roundOngoing;}

	public void setObserving(boolean a) { observingAllowed = a;}
	public boolean isObservingAllowed() { return observingAllowed;}

	/** Chooses who's turn it is. The first player is chosen randomly, 
	 * then it cycles through the list of players.
	 * @return the index of the next player
	 */
	public int nextPlayer() {
		if (currentPlayer == -1) {
			currentPlayer = ThreadLocalRandom.current().nextInt(0, players.size());
		} else {
			currentPlayer = (currentPlayer+1)%players.size();
		}
		return currentPlayer;
	}

	/** @param p Player to add to the list of players  */
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	/** @return the list of players  */
	public ArrayList<Player> getPlayers() {return players;}
	
	/** @param p Producer to add to the list of producers */
	public void addProducer(Producer p) {
		producers.add(p);
	}
	
	/** @return the list of producers  */
	public ArrayList<Producer> getProducers() {return producers;}

	/** {@inheritDoc} */
	public void playerFinished(int id) throws RemoteException {
		nbFinishedPlayers++;
		if (hasTurns) {
			finishedPlayers.add(currentPlayer);
			if (nbFinishedPlayers == 1) {
				winner = currentPlayer;
			}
		} else {
			if (nbFinishedPlayers == 1) {
				winner = id;
			}
		}
		if (nbFinishedPlayers == players.size()) {
			roundOngoing = false;
		}
	}
	
	/** {@inheritDoc} */
	public synchronized void turnFinished() throws RemoteException {
		waitingForMove = false;
		notify();
	}
	
	public synchronized void waitForMove() {
		if (finishedPlayers.contains(currentPlayer)) {
			waitingForMove = false;
		} else {
			while(waitingForMove) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
		}
	}
	
	
	/** @return index of the first player to finish the round */
	public int getWinner() { return winner; }
	
	/** 
	 * Signals to all producers to stop their productions.
	 * @exception RemoteException exception occurred during remote call
	 */
	public void stopProduction() throws RemoteException {
		for (int i=0; i<producers.size(); i++) {
			producers.get(i).stopProduction();
		}
	}
	
	/**
	 * Starts a new round of the game. Through the GameCoordinator,
	 * the RoundCoordinator knows all players and producers.
	 * @param args		the command line options
	 */
	public static void main(String args[]) {
		if (args.length != 3) {
			System.out.println("Usage : java RoundCoordinatorImpl <port>"+
								" <GameCoord Host> <GameCoord Port>") ;
			System.exit(0) ;
		}
	
		String bindname="rmi://localhost:"+args[0]+"/RoundCoordinator";

		try {
			String hostIP = InetAddress.getLocalHost().getHostAddress();
			
			/* access game coordinator */
			GameCoordinator gameCoord = (GameCoordinator) Naming.lookup(
				"rmi://" + args[1] + ":" + args[2] + "/GameCoordinator");
			
			/* create accessible object for this round */
			RoundCoordinatorImpl roundCoord = new RoundCoordinatorImpl();
			Naming.rebind(bindname,roundCoord) ;
			System.out.println("RoundCoordinator running");
			
			/* Get information from GameCoordinator */
			ArrayList<Agent> playerLocations = gameCoord.getPlayers();
			Map<Agent,Integer> producerLocations = gameCoord.getProducers();
			if (playerLocations.size() < 2 ) {
				System.err.println("Not enough players. Ending...");
				Naming.unbind(bindname);
				System.exit(1);
			} 
			if (producerLocations.size() == 0 ) {
				System.err.println("No producers found. Ending...");
				Naming.unbind(bindname);
				System.exit(1);
			} 
			
			/* ask for paramterers */
			Set<Integer> resourcesSet = new HashSet<Integer>();
			resourcesSet.addAll(producerLocations.values());
			Scanner reader = new Scanner(System.in);
			Map<Integer,Integer> objectives = new HashMap<Integer,Integer>();
			int o = 51;
			for (Integer r : resourcesSet) {
				System.out.println("Set objective for R"+r+"(>=50): ");
				o = reader.nextInt();
				reader.nextLine();
				while (o < 50) {
					System.out.println("Not a possible number. Try again: ");
					o = reader.nextInt();
					reader.nextLine();
				}
				objectives.put(r,o);
			}
			
			System.out.println("Set max number of copies that can be taken at once:");
			int maxN = reader.nextInt();
			reader.nextLine();
			while (maxN <= 0) {
				System.out.println("Not a possible number. Try again: ");
				maxN = reader.nextInt();
				reader.nextLine();
			}
			
			if (gameCoord.isHumanPresent()) {
				roundCoord.setTurns(true);
			} else {
				System.out.println("Should the players take turns? (y/n)");
				String text = reader.nextLine();
				boolean answer = text.toLowerCase().matches("^[yo]");
				roundCoord.setTurns(answer);
			}
			
			System.out.println("Is observing allowed? (y/n)");
			boolean answer = reader.nextLine().toLowerCase().matches("^[yo]");
			roundCoord.setObserving(answer);
			
			System.out.println("Contacting players");
			
			/* Tell players where to find competitors and resources */
			int id = 0;
			Iterator<Agent> playerIter = playerLocations.iterator();
			while (playerIter.hasNext()) {
				Agent a = playerIter.next();
				Player p = (Player) Naming.lookup("rmi://"+ 
						a.getHost()+ ":" + a.getPort() + "/Player");
				roundCoord.addPlayer(p);
				p.setPlayers(playerLocations);
				p.setProducers(producerLocations);
				p.setRoundCoordinator(hostIP,args[0]);
				p.setObjective(objectives);
				p.setObserving(roundCoord.isObservingAllowed());
				p.setID(id);
				id++;
			}
			
			System.out.println("Contacting producers");
			
			/* Tell producers to start producing */
			Iterator<Agent> prodIter = producerLocations.keySet().iterator();
			while (prodIter.hasNext()) {
				Agent a = prodIter.next();
				Producer p = (Producer) Naming.lookup("rmi://"+ a.getHost()+ 
										":" + a.getPort() + "/Producer");
				roundCoord.addProducer(p);
				p.startProduction();
				p.setMaxTaken(maxN);
			}
			
			/* mark the start of the game */
			roundCoord.setRoundOngoing();
			
			/* if players take turns, coordonate them */
			if (roundCoord.isTurnsSet()) {
				int next = roundCoord.nextPlayer();
				System.out.println("Starting with player "+next);
				while (roundCoord.isRoundOngoing()) {
					Player p = roundCoord.getPlayers().get(next);
					roundCoord.waitingForMove = true;
					p.setMyTurn(true);
					roundCoord.waitForMove();
					next = roundCoord.nextPlayer();
				}
			} else {
				System.out.println("Round ongoing...");
				while (roundCoord.isRoundOngoing()) {
				}
			}
			
			System.out.println("Round over");
			System.out.println("The winner is player "+roundCoord.getWinner());
			roundCoord.stopProduction();
			//gameCoord.endGame();
			//Naming.unbind(bindname) ;
			System.exit(0);
		} 
		catch (RemoteException re) { System.err.println(re) ;System.exit(1); }
		catch (MalformedURLException e) { System.err.println(e) ;System.exit(1); }
		catch (NotBoundException re) { System.err.println(re) ;System.exit(1); }
		catch (UnknownHostException e) {System.err.println(e) ;System.exit(1);}
		catch (Exception e) {System.err.println(e) ;System.exit(1);}
	}
}
