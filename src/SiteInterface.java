import java.rmi.*;

public interface SiteInterface extends Remote
{
    public boolean didIRequestTheCriticalSection() throws RemoteException;
    public void requestCriticalSection() throws RemoteException;
    public void receiveExternalRequest(int i, int sn) throws RemoteException;

    public void takeToken() throws RemoteException;
    public void releaseCriticalSection() throws RemoteException;

    public boolean canIExecuteTheCriticalSection() throws RemoteException;
    public void startExecutingTheCriticalSection() throws RemoteException;
    public void finishTheExecutionOfTheCriticalSection() throws RemoteException;

    public boolean shouldIKillMyself() throws RemoteException;

    public int getId() throws RemoteException;
}
