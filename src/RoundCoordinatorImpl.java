import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.net.MalformedURLException ;
import java.util.Scanner; 
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

public class RoundCoordinatorImpl 
		extends UnicastRemoteObject 
		implements RoundCoordinator 
{
	private boolean roundOngoing = false;
	private int delay;
	private int endType;
	private int personalityType;
	
	public RoundCoordinatorImpl() throws RemoteException {}

	/**
	 * @brief Marks the round as finished.
	 */
	public void endRound() throws RemoteException {
		roundOngoing = false;
		//notify GameCoordinator ? 
	}
	
	public static void main(String args[]) {
		if (args.length != 3) {
			System.out.println("Usage : java RoundCoordinatorImpl <port>"+
								" <GameCoord Host> <GameCoord Port>") ;
			System.exit(0) ;
		}
		
		String bindname="rmi://localhost:"+args[0]+"/RoundCoordinator";
		
		try {
			/* access game coordinator */
			GameCoordinator gameCoord = (GameCoordinator) Naming.lookup(
				"rmi://" + args[1] + ":" + args[2] + "/GameCoordinator");
			
			/* create accessible object for this round */
			RoundCoordinatorImpl roundCoord = new RoundCoordinatorImpl();
			Naming.bind(bindname,roundCoord) ;
			System.out.println("round Coordinator running");
			
			/* ask for paramterers */
			Scanner reader = new Scanner(System.in);
			System.out.println("Set the number of ressources to find (>=50): ");
			int objective = reader.nextInt();
			while (objective < 50) {
				System.out.println("Not a possible number. Try again: ");
				objective = reader.nextInt();
			}
			
			/* Get information from GameCoordinator */
			ArrayList<Agent> playerLocations = gameCoord.getPlayers();
			Map<Agent,Integer> producerLocations = gameCoord.getProducers();
			if (playerLocations.size() == 0 ) {
				System.err.println("No players found. Ending...");
				System.exit(1);
			} 
			if (producerLocations.size() == 0 ) {
				System.err.println("No producers found. Ending...");
				System.exit(1);
			} 
			
			/* Tell players where to find competitors and ressources */
			ArrayList<Player> players = new ArrayList<Player>();
			Iterator<Agent> playerIter = playerLocations.iterator();
			while (playerIter.hasNext()) {
				Agent a = playerIter.next();
				Player p = (Player) Naming.lookup("rmi://"+ a.getHost()+ 
										":" + a.getPort() + "/Player");
				players.add(p);
				p.setObjective(objective);
				p.setPlayers(playerLocations);
				p.setProducers(producerLocations);
			}
			
			
		} 
		catch (RemoteException re) { System.err.println(re) ; }
		catch (AlreadyBoundException e) { System.err.println(e) ; }
		catch (MalformedURLException e) { System.err.println(e) ; }
		catch (NotBoundException re) { System.err.println(re) ; }
	}
}
