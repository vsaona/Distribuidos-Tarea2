import java.rmi.*;

public interface Reader extends Remote
{
	public void request(Integer id, Integer seq) throws RemoteException;
	public void takeToken(Token arrivingToken) throws RemoteException;
	public void waitToken() throws Exception;
	public void kill() throws RemoteException;
}
