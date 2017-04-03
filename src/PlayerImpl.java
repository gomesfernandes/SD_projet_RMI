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
//import Player ;

public class PlayerImpl 
		extends UnicastRemoteObject 
		implements Player 
{
	private Map<Integer,Integer> objective;
	private ArrayList<Producer> competitors;
	private ArrayList<Player> producers;
	private boolean myTurn;
	
	public PlayerImpl() throws RemoteException {}
	
	public void setObjective(Map<String,Integer> objective) throws RemoteException {}
	public void setPlayers(Map<String,Integer> j) throws RemoteException {}
	public void setProducers(Map<String,Integer> p) throws RemoteException {}
	
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
			PlayerImpl j = new PlayerImpl();
			Naming.bind(bindname,j);
			
			/* access game coordinator and notify him about us */
			GameCoordinator gameCoord = (GameCoordinator) Naming.lookup(
				"rmi://" + args[1] + ":" + args[2] + "/GameCoordinator");
			gameCoord.addPlayer(hostIP,args[0]);
			
			
			System.out.println("Player running");
			
		} 
		catch (RemoteException re) { System.out.println(re) ; }
		catch (AlreadyBoundException e) { System.out.println(e) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (UnknownHostException re) { System.out.println(re) ; }
		
	}
}
