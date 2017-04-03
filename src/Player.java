import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Player extends Remote {
	public void setObjective(Map<String,Integer> objective) throws RemoteException ;
	public void setPlayers(Map<String,Integer> j) throws RemoteException;
	public void setProducers(Map<String,Integer> p) throws RemoteException;
	public void setMyTurn(boolean b) throws RemoteException;
	
	//public int getRessource(int type, int n) throws RemoteException;
}
