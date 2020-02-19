import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Math;

class Site extends UnicastRemoteObject implements SiteInterface
{
    private Extractor reader;
    private int myId;
    private Integer[] RN;
    private boolean hasToken = false;
    private boolean isExcuting = false;
    private boolean isRequesting = false;

    boolean everythingIsFine = true;

    public Site() throws RemoteException
	{
		super();
    }

    public Site(Extractor reader, int procesess, int myId) throws RemoteException
    {
		super();
        this.reader = reader;
        this.myId = myId;
        assert(myId < procesess);
        this.RN = new Integer[procesess];
        for(int i = 0; i < procesess; ++i){
            this.RN[i] = 0;
        }
    }

    public int giveMeTheRNof(int id) throws RemoteException
    {
        return RN[id];
    }

    public boolean didIRequestTheCriticalSection() throws RemoteException
    {
        return isRequesting;
    }

    public void requestCriticalSection() throws RemoteException
    {
        if(!hasToken && !isRequesting) {
            try {
                int sn = RN[myId] + 1;
                reader.request(myId, sn);
                RN[myId] = sn;
                isRequesting = true;
            } catch (RemoteException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void receiveExternalRequest(int i, int sn) throws RemoteException
    {
        assert(i < RN.length);
        assert(i != myId);
        RN[i] = Math.max(RN[i], sn);

        if(!isExcuting && hasToken) {
            try {
                reader.sendTokenTo(i, RN[i]);
                hasToken = false;
            } catch (RemoteException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void takeToken() throws RemoteException
    {
        hasToken = true;
        isRequesting = false;
    }

    public void releaseCriticalSection() throws RemoteException
    {
        try {
            if(hasToken && !isExcuting && reader.releaseCriticalSection(myId, RN)) {
                hasToken = false;
            }
        } catch(RemoteException e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean canIExecuteTheCriticalSection() throws RemoteException
    {
        return hasToken;
    }

    public void startExecutingTheCriticalSection() throws RemoteException
    {
        assert(hasToken && !isExcuting);
        isExcuting = true;
    }

    public void finishTheExecutionOfTheCriticalSection() throws RemoteException
    {
        assert(hasToken && isExcuting);
        isExcuting = false;
    }

    public boolean shouldIKillMyself() throws RemoteException
    {
        if(!everythingIsFine){
            return true;
        }
        return false;
    }

    public int getId() throws RemoteException
    {
        return myId;
    }
}