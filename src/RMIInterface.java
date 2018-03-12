import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote{

	
	public int[] readData(int id) throws RemoteException;
	public int[] writeData(int id) throws RemoteException;
//	public void endConnection() throws RemoteException;
	
	
}
