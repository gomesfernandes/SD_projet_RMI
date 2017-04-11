/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RoundCoordinator extends Remote {
	public void playerFinished() throws RemoteException;
}
