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
import java.util.ArrayList;
import java.util.Iterator;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

public class RoundCoordinatorImpl 
		extends UnicastRemoteObject 
		implements RoundCoordinator 
{
	private boolean roundOngoing = true;
	private int delay;
	private int endType;
	private int personalityType;
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Producer> producers = new ArrayList<Producer>();
	private int nbFinishedPlayers = 0;
	private int currentPlayer = -1;
	private int winner = -1;
	
	public RoundCoordinatorImpl() throws RemoteException {}

	/** @return true if the round is finished, false if not */
	public boolean isRoundOngoing() { return roundOngoing; }

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
	public void playerFinished() throws RemoteException {
		nbFinishedPlayers++;
		if (nbFinishedPlayers == 1) {
			winner = currentPlayer;
		}
		if (nbFinishedPlayers == players.size()) {
			roundOngoing = false;
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
			System.out.println("round Coordinator running");
			
			/* Get information from GameCoordinator */
			ArrayList<Agent> playerLocations = gameCoord.getPlayers();
			Map<Agent,Integer> producerLocations = gameCoord.getProducers();
			if (playerLocations.size() == 0 ) {
				System.err.println("No players found. Ending...");
				Naming.unbind(bindname);
				System.exit(1);
			} 
			if (producerLocations.size() == 0 ) {
				System.err.println("No producers found. Ending...");
				Naming.unbind(bindname);
				System.exit(1);
			} 
			
			/* ask for paramterers */
			/*
			Set<Integer> resourcesSet = new HashSet<Integer>();
			resourcesSet.addAll(producerLocations.values());
			Scanner reader = new Scanner(System.in);
			int objective = 51;
			for (Integer s : resourcesSet) {
				System.out.println("Set objective for R"+s+"(>=50): ");
				objective = reader.nextInt();
				while (objective < 50) {
					System.out.println("Not a possible number. Try again: ");
					objective = reader.nextInt();
				}
			}
			*/
			
			Scanner reader = new Scanner(System.in);
			System.out.println("Set the number of resources to find (>=50): ");
			int objective = reader.nextInt();
			while (objective < 50) {
				System.out.println("Not a possible number. Try again: ");
				objective = reader.nextInt();
			}
			
			/* Tell players where to find competitors and resources */
			Iterator<Agent> playerIter = playerLocations.iterator();
			while (playerIter.hasNext()) {
				Agent a = playerIter.next();
				Player p = (Player) Naming.lookup("rmi://"+ 
						a.getHost()+ ":" + a.getPort() + "/Player");
				roundCoord.addPlayer(p);
				p.setObjective(objective);
				p.setPlayers(playerLocations);
				p.setProducers(producerLocations);
				p.setRoundCoordinator(hostIP,args[0]);
			}
			
			/* Tell producers to start producing */
			Iterator<Agent> prodIter = producerLocations.keySet().iterator();
			while (prodIter.hasNext()) {
				Agent a = prodIter.next();
				Producer p = (Producer) Naming.lookup("rmi://"+ a.getHost()+ 
										":" + a.getPort() + "/Producer");
				roundCoord.addProducer(p);
				p.startProduction();
			}
			
			int next = roundCoord.nextPlayer();
			System.out.println("Stating with player "+next);
			while (roundCoord.isRoundOngoing()) {
				Player p = roundCoord.getPlayers().get(next);
				p.setMyTurn(true);
				next = roundCoord.nextPlayer();
				Thread.sleep(1000);
			}
			
			System.out.println("Round over");
			System.out.println("The winner is player "+roundCoord.getWinner());
			roundCoord.stopProduction();
			//gameCoord.endGame();
			System.exit(0);
		} 
		catch (RemoteException re) { System.err.println(re) ; }
		catch (MalformedURLException e) { System.err.println(e) ; }
		catch (NotBoundException re) { System.err.println(re) ; }
		catch (UnknownHostException e) {}
		catch (Exception e) {}
	}
}
