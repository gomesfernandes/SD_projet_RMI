/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.ArrayList;

public interface Player extends Remote {
	public void setObjective(int o) throws RemoteException ;
	public void setPlayers(ArrayList<Agent> j) throws RemoteException;
	public void setProducers(Map<Agent,Integer> p) throws RemoteException;
	public void setMyTurn(boolean b) throws RemoteException;
	public void setRoundCoordinator(String host, String port) 
												throws RemoteException ;
	
	//public int getResource(int type, int n) throws RemoteException;
}
