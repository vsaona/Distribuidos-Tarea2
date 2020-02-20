import java.rmi.*;
import java.rmi.server.*;

import javax.naming.SizeLimitExceededException;

public class Extractor extends UnicastRemoteObject implements Reader
{
	private static final long serialVersionUID = 5258238654722105827L;
	int processes;
	long originalFileSize;
	Token token;

	private int nextId = 0;
	private SiteInterface[] sitesArr;

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
		this.sitesArr = new SiteInterface[n];
	}

	public long originalSize()  throws RemoteException
	{
		return originalFileSize;
	}

	public void request(Integer id, Integer seq) throws RemoteException
	{
		boolean didIdGotTheToken = false;
		for(int j = 0; j < nextId; ++j){
			if(j != id){
				didIdGotTheToken = sitesArr[j].receiveExternalRequest(id, seq) || didIdGotTheToken;
			}
		}
		if(!didIdGotTheToken) {
			sitesArr[id].waitToken();
		}
	}

	public void killEveryone() throws RemoteException
	{
		for(int i = 0; i < nextId; ++i) {
			sitesArr[i].kill();
		}
	}


	public void registerSite(SiteInterface site) throws RemoteException, SizeLimitExceededException, RuntimeException
	{
		assert(nextId < processes);
		if(nextId >= processes) {
			throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
		}
		Utils.debugMsg(-1, "Llego un nuevo proceso. Asignando id " + nextId + ".");
		site.setId(nextId, processes);
		if(nextId == 0) {
			site.takeToken();
		}
		sitesArr[nextId++] = site;
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
