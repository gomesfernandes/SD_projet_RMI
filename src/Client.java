import java.rmi.* ;
import java.net.MalformedURLException ;

public class Client
{
	public static void main(String [] args)
	{
		if (args.length != 2)
		{
			System.out.println(
			"Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
			System.exit(0) ;
		}
		try
		{
			GameCoordinator stub = (GameCoordinator) Naming.lookup(
			"rmi://" + args[0] + ":" + args[1] + "/GameCoordinator") ;
			// stub est désormais utilisable comme un objet local.
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
}
