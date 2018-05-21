import java.rmi.Remote;
import java.rmi.RemoteException;

public interface P extends Remote {
	public void startElection(String challengerPid) throws RemoteException;
	public void setLeader(String nodePid) throws RemoteException;
	public void ok() throws RemoteException;
}
