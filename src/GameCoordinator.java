import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.ArrayList;

public interface GameCoordinator extends Remote {
	public void endGame() throws RemoteException;
	public void addProducer(String host,String port, Integer r) 
												throws RemoteException;
	public void addPlayer(String host,String port) throws RemoteException;
	public void setGameOngoing() throws RemoteException;
	public int getNumberPlayers() throws RemoteException;
	public int getNumberProducers() throws RemoteException;
	public ArrayList<Agent> getPlayers() throws RemoteException;
	public Map<Agent,Integer> getProducers() throws RemoteException;
}
