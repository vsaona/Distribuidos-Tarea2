import java.rmi.*;

import javax.naming.SizeLimitExceededException;

public interface SiteInterface extends Remote
{
    public void setId(int id, int bearerProcesess) throws RemoteException, RuntimeException, SizeLimitExceededException;
    public int giveMeTheRNof(int id) throws RemoteException;

    // Returns true if this site had the token and gave it to i.
    public boolean receiveExternalRequest(int i, int sn) throws RemoteException;
    public void takeToken() throws RemoteException;
	public void waitToken() throws RemoteException;
	public void kill() throws RemoteException;
}
