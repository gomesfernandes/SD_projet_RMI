import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.ArrayList;

public interface Player extends Remote {
	public void setObjective(int o) throws RemoteException ;
	public void setPlayers(ArrayList<Agent> j) throws RemoteException;
	public void setProducers(Map<Agent,Integer> p) throws RemoteException;
	public void setMyTurn(boolean b) throws RemoteException;
	
	//public int getRessource(int type, int n) throws RemoteException;
}
