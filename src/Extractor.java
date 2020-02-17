import java.rmi.*;
import java.rmi.server.*;

public class Extractor extends UnicastRemoteObject implements Reader{

	String fileName;
	int capacity;
	int speed;
	boolean hasToken;
	Token token;

	private void createToken(Integer n) {
		this.hasToken = true;
		this.token = new Token(n);
	}

	Extractor(int n, String fileName, int capacity, int speed, boolean hasToken) throws RemoteException
	{
		super();
		this.fileName = fileName;
		this.capacity = capacity;
		this.speed = speed;
		if(hasToken) {
			this.createToken(n);
		} else {
			this.hasToken = false;
		}
	}

	public void request(Integer id, Integer seq) throws RemoteException
	{
		token.request(id);
	}

	public void takeToken(Token arrivingToken) throws RemoteException
	{
		token = arrivingToken;
	}

	public void waitToken() throws Exception
	{
		Thread.sleep(1000);
	}

	public void kill() throws RemoteException
	{
		System.exit(0);
	}
}
