import java.rmi.*;

public interface Reader extends Remote
{
	public void request(Integer id, Integer seq) throws RemoteException;
	public void waitToken() throws Exception;
	public void kill() throws RemoteException;

	public SiteInterface generateSite() throws RemoteException;
	public void sendTokenTo(int id, int sn) throws RemoteException;
	/// Returns true if token has been sended to other process.
	public boolean releaseCriticalSection(int id, Integer[] RN) throws RemoteException;
}
