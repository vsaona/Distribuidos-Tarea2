import java.rmi.*;
import java.rmi.server.*;

public class Extractor extends UnicastRemoteObject implements Reader
{
	private static final long serialVersionUID = 5258238654722105827L;
	int processes;
	long originalFileSize;
	Token token;

	private int nextId = 0;
	private Site[] sitesArr;

	Extractor() throws RemoteException
	{
		super();
	}

	Extractor(int n, long originalFileSize) throws RemoteException
	{
		super();
		this.processes = n;
		this.originalFileSize = originalFileSize;
		this.token = new Token(n);
		this.sitesArr = new Site[n];
	}

	public long originalSize()  throws RemoteException
	{
		return originalFileSize;
	}

	public void request(Integer id, Integer seq) throws RemoteException
	{
		for(int j = 0; j < nextId; ++j){
			if(j != id){
				sitesArr[j].receiveExternalRequest(id, seq);
			}
		}
	}

	public void waitToken() throws Exception
	{
		// TODO
		Utils.sleep(-2, 1000);
	}

	public void kill() throws RemoteException
	{
		for(int i = 0; i < nextId; ++i) {
			sitesArr[i].everythingIsFine = false;
		}
	}
	public void killEveryone() throws RemoteException
	{
		kill();
	}


	public SiteInterface generateSite() throws RemoteException
	{
		assert(nextId < processes);
		sitesArr[nextId] = new Site(this, processes, nextId);
		if(nextId == 0) {
			sitesArr[nextId].takeToken();
		}
		return sitesArr[nextId++];
	}

	public boolean sendTokenTo(int id, int sn) throws RemoteException
	{
		assert(id < sitesArr.length);
		if(sn == token.executed[id] + 1) {
			sitesArr[id].takeToken();
			return true;
		}
		return false;
	}

	public boolean releaseCriticalSection(int id) throws RemoteException
	{
		assert(id < sitesArr.length);
		token.executed[id] = sitesArr[id].giveMeTheRNof(id);

		for(int j = 0; j < nextId; ++j) {
			Utils.debugMsg(id, ""+j + " ~ " + nextId);
			//Utils.debugMsg("length: " + RN.length);
			int left = sitesArr[id].giveMeTheRNof(j);
			int right = token.executed[j] + 1;
			if(left == right) {
				if(!token.queue.contains(j)) {
					Utils.debugMsg(id, "Agreando a " + j + " a la cola.");
					token.queue.add(j);
				}
			}
		}

		if(token.queue.isEmpty()){
			return false;
		}

		int j = token.queue.remove();
		Utils.debugMsg(id, "Mandando token a " + j + ".");
		sitesArr[j].takeToken();
		return true;
	}
}
