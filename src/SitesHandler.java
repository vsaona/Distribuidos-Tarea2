import java.rmi.RemoteException;

import javax.management.relation.InvalidRelationIdException;
import javax.naming.SizeLimitExceededException;

public class SitesHandler
{
    private int processes;
    private int myId;

    private int lastGeneratedId = 0;
    private int registeredIds = 1;
    private int[] RN;
    private SiteInterface[] sitesArr;

    public SitesHandler(Site site, RMIStuff rmi, int processes, int myId) throws SizeLimitExceededException, RemoteException, InterruptedException, InvalidRelationIdException
    {
        assert(myId >= 0);
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
            ++this.registeredIds;
        }
    }

    public int getRN(int i)
    {
        assert(i < registeredIds);
        return RN[i];
    }

    public SiteInterface getSite(int i) throws RemoteException
    {
        assert(i < registeredIds);
        SiteInterface site = sitesArr[i];
        if(site == null) {
            throw new IllegalStateException("El Site " + i + " es null, pero no deberia serlo.");
        }
        int siteId = site.getId();
        if(site.getId() != i) {
            throw new IllegalStateException("El Site con id " + siteId + " estaba guardado en la posicion " + i + ".");
        }
        return site;
    }

    public String rnAsStr()
    {
        return Utils.arrayToStr(RN);
    }

    public int generateNewId() throws SizeLimitExceededException
    {
        assert(myId == 0);
        if(registeredIds >= processes) {
		    throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        ++lastGeneratedId;
        Utils.debugMsg(myId, "Generando nuevo id: " + lastGeneratedId + ".");
        return lastGeneratedId;
    }

    public void registerMe(SiteInterface otherSite) throws RemoteException, SizeLimitExceededException, InvalidRelationIdException
    {
        assert(registeredIds != myId);
        int otherId = otherSite.getId();
        Utils.debugMsg(myId, "Recibi un nuevo proceso con id: " + otherId + ". Yo espero que tanga la id: " + registeredIds + ".");
        assert(registeredIds == otherId);
        if(registeredIds >= processes) {
		    throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        if(otherId == myId) {
            Utils.debugErr(myId, "Alguien intenta suplantarme. Ayuda :s");
            throw new InvalidRelationIdException("Estas intentando registrate con mi Id. Baka!!.");
        }
        if(otherId < registeredIds) {
            throw new InvalidRelationIdException("Estas intentando registrate una Id ya registrada.");
        }
        this.sitesArr[registeredIds++] = otherSite;
    }

    public int usableLengthRN()
    {
        assert(registeredIds <= processes);
        return registeredIds;
    }

    public int incrementMyRN()
    {
        RN[myId] += 1;
        return RN[myId];
    }

    public void updateRN(int i, int sn)
    {
        assert(i < registeredIds);
        RN[i] = Math.max(RN[i], sn);
    }

    public boolean requestEveryone(int sn) throws RemoteException
    {
        boolean didIGotTheToken = false;
        for(int j = 0; j < registeredIds; ++j) {
            if(j != myId) {
                didIGotTheToken = this.sitesArr[j].request(myId, sn) || didIGotTheToken;
            }
        }
        return didIGotTheToken;
    }

    public void killEveryone() throws RemoteException
    {
        Utils.debugMsg(myId, "Asesinatos, asesinatos en masa!");
        for(int j = 0; j < registeredIds; ++j) {
            if(j != myId) {
                this.sitesArr[j].kill();
            }
        }
    }
}
