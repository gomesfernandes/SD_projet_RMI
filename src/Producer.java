import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Producer extends Remote {
	public void startProduction() throws RemoteException ;
	public int takeCopies(int n) throws RemoteException ;
}
