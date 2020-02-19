import java.rmi.*;
import java.rmi.server.*;

public class Extractor extends UnicastRemoteObject implements Reader
{
	int processes;
	String fileName;
	int capacity;
	int speed;
	Token token;

	private int nextId = 0;
	private Site[] sitesArr;

	Extractor() throws RemoteException
	{
		super();
	}

	Extractor(int n, String fileName, int capacity, int speed) throws RemoteException
	{
		super();
		this.processes = n;
		this.fileName = fileName;
		this.capacity = capacity;
		this.speed = speed;
		this.token = new Token(n);
		this.sitesArr = new Site[n];
	}

	public void request(Integer id, Integer seq) throws RemoteException
	{
		for(int i = 0; i < nextId; ++i){
			if(i != id){
				sitesArr[i].receiveExternalRequest(i, seq);
			}
		}
	}

	public void waitToken() throws Exception
	{
		// TODO
		Thread.sleep(1000);
	}

	public void kill() throws RemoteException
	{
		// TODO
		System.exit(0);
	}


	public SiteInterface generateSite() throws RemoteException
	{
		assert(nextId < processes);
		sitesArr[nextId] = new Site(this, processes, nextId);
		return sitesArr[nextId++];
	}

	public void sendTokenTo(int id, int sn) throws RemoteException
	{
		assert(id < sitesArr.length);
		if(sn == token.executed[id] + 1) {
			sitesArr[id].takeToken();
		}
	}

	public boolean releaseCriticalSection(int id, Integer[] RN) throws RemoteException
	{
		assert(id < sitesArr.length);
		token.executed[id] = RN[id];

		for(int j = 0; j < nextId; ++j) {
			if(RN[j] == token.executed[j] + 1) {
				if(!token.queue.contains(j)) {
					token.queue.add(j);
				}
			}
		}

		if(token.queue.isEmpty()){
			return false;
		}

		sitesArr[token.queue.remove()].takeToken();
		return true;
	}
}
