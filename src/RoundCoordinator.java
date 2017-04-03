import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RoundCoordinator extends Remote {
	public void endRound() throws RemoteException;
}
