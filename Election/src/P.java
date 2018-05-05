import java.rmi.Remote;
import java.rmi.RemoteException;

public interface P extends Remote {
	public void startElection() throws RemoteException;
	public void setLeader() throws RemoteException;
}
