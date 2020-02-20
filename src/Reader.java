import java.rmi.*;

public interface Reader extends Remote
{
	public void request(Integer id, Integer seq) throws RemoteException;
	public void waitToken() throws Exception;
	public void kill() throws RemoteException;
	public void killEveryone() throws RemoteException;

	public long originalSize()  throws RemoteException;
	public SiteInterface generateSite() throws RemoteException;
	public boolean sendTokenTo(int id, int sn) throws RemoteException;
	/// Returns true if token has been sended to other process.
	public boolean releaseCriticalSection(int id) throws RemoteException;
}
