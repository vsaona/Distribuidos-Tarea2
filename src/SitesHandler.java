import java.rmi.RemoteException;

import javax.naming.SizeLimitExceededException;

public class SitesHandler
{
    private int processes;
    private int myId;

    private int nextId = 1;
    private int[] RN;
    private SiteInterface[] sitesArr;

    public SitesHandler(Site site, RMIStuff rmi, int processes, int myId) throws SizeLimitExceededException, RemoteException, InterruptedException
    {
        if(myId >= processes) {
		throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        this.processes = processes;
        this.myId = myId;
        this.RN = new int[processes];
        this.sitesArr = new SiteInterface[processes];
        for(int i = 0; i < processes; ++i) {
            this.RN[i] = 0;
            this.sitesArr[i] = null;
        }
        this.sitesArr[myId] = site;

        for(int i = 0; i < myId; ++i) {
            this.sitesArr[i] = (SiteInterface)rmi.getObject(i);
            this.sitesArr[i].registerMe(site);
            ++this.nextId;
        }
    }

    public int getRN(int i)
    {
        assert(i < nextId);
        return RN[i];
    }

    public SiteInterface getSite(int i) throws RemoteException
    {
        assert(i < nextId);
        SiteInterface site = sitesArr[i];
        if(site == null) {
            throw new IllegalStateException("El Site " + i + " es null, pero no deberia serlo.");
        }
        int siteId =site.getId();
        if(site.getId() != i) {
            throw new IllegalStateException("El Site con id " + siteId + " estaba guardado en la posicion " + i + ".");
        }
        return site;
    }

    public String rnAsStr()
    {   
        String RNstr = "[(0, " + RN[0] + ")";
        for(int i = 1; i < RN.length; ++i) {
            RNstr += ", (" + i + ", " + RN[i] + ")";
        }
        RNstr += "]";
        return RNstr;
    }

    public int generateNewId() throws SizeLimitExceededException
    {
        assert(myId == 0);
        if(nextId >= processes) {
		throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
	Utils.debugMsg(myId, "Generando nuevo id: " + nextId + ".");
        return nextId;
    }

    public void registerMe(SiteInterface otherSite) throws RemoteException, SizeLimitExceededException
    {
	assert(nextId != myId);
        Utils.debugMsg(myId, "Recibi un nuevo proceso con id: " + otherSite.getId() + ". Yo espero que tanga la id: " + nextId + ".");
        assert(nextId == otherSite.getId());
        if(nextId >= processes) {
		throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        this.sitesArr[nextId++] = otherSite;
    }

    public int usableLengthRN()
    {
        return nextId;
    }

    public int incrementMyRN()
    {
        RN[myId] += 1;
        return RN[myId];
    }

    public void updateRN(int i, int sn)
    {
        assert(i < nextId);
        RN[i] = Math.max(RN[i], sn);
    }

    public boolean requestEveryone(int sn) throws RemoteException
    {
        boolean didIGotTheToken = false;
        for(int j = 0; j < nextId; ++j) {
            if(j != myId) {
                didIGotTheToken = this.sitesArr[j].request(myId, sn) || didIGotTheToken;
            }
        }
        return didIGotTheToken;
    }

    public void killEveryone() throws RemoteException
    {
        Utils.debugMsg(myId, "Asesinatos, asesinatos en masa!");
        for(int j = 0; j < nextId; ++j) {
            if(j != myId) {
                this.sitesArr[j].kill();
            }
        }
    }
}
