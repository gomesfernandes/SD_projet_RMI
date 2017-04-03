import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.* ;
import java.rmi.* ;
import java.net.MalformedURLException ;

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
								"<GameCoord Host> <GameCoord Port>") ;
			System.exit(0) ;
		}
		
		String bindname="rmi://localhost:"+args[0]+"/RoundCoordinator";
		
		try {
			RoundCoordinatorImpl roundCoord = new RoundCoordinatorImpl();
			Naming.bind(bindname,roundCoord) ;
			System.out.println("round Coordinator running");
			
		} 
		catch (RemoteException re) { System.out.println(re) ; }
		catch (AlreadyBoundException e) { System.out.println(e) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
}
