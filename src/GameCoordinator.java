import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface GameCoordinator extends Remote {
	public void endGame() throws RemoteException;
	public void addProducer(String host,String port, Integer r) 
												throws RemoteException;
	public void addPlayer(String host,String port) throws RemoteException;
	public void setGameOngoing() throws RemoteException;
}
