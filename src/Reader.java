import java.rmi.*;

import javax.naming.SizeLimitExceededException;

public interface Reader extends Remote
{
	public void request(Integer id, Integer seq) throws RemoteException;
	public void killEveryone() throws RemoteException;

	public long originalSize()  throws RemoteException;
	public void registerSite(SiteInterface site) throws RemoteException, SizeLimitExceededException, RuntimeException;
	public boolean sendTokenTo(int id, int sn) throws RemoteException;
	/// Returns true if token has been sended to other process.
	public boolean releaseCriticalSection(int id) throws RemoteException;
}
