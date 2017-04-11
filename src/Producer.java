/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Producer extends Remote {
	public void startProduction() throws RemoteException ;
	public void stopProduction() throws RemoteException ;
	public int takeCopies(int n) throws RemoteException ;
	public int getResourceType() throws RemoteException ;
}
