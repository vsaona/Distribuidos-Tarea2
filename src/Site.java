import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Math;

class Site extends UnicastRemoteObject implements SiteInterface
{
    private static final long serialVersionUID = 8575564069279616010L;

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

    boolean _isInValidState()
    {
        if(hasToken) {
            if(isExcuting) {
                if(isRequesting) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if(isRequesting) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            if(isExcuting) {
                return false;
            } else {
                return true;
            }
        }
    }

    boolean isInValidState()
    {
        if(!_isInValidState()) {
            Utils.debugErr(myId, "Site invalid state:");
            Utils.debugErr(myId, "\thasToken: " + hasToken);
            Utils.debugErr(myId, "\tisExcuting: " + isExcuting);
            Utils.debugErr(myId, "\tisRequesting: " + isRequesting);
            return false;
        }
        return true;
    }

    public int giveMeTheRNof(int id) throws RemoteException
    {
        assert(isInValidState());
        return RN[id];
    }

    public boolean didIRequestTheCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        return isRequesting;
    }

    public void requestCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        if(!hasToken && !isRequesting) {
            try {
                Utils.debugMsg(myId, "Gente! Quiero el token!");
                RN[myId] = RN[myId] + 1;
                isRequesting = true;
                reader.request(myId, RN[myId]);
            } catch (RemoteException e) {
                System.err.println(e.toString());
                // e.printStackTrace(System.err);
                reader.killEveryone();
            }
        }
    }

    public void receiveExternalRequest(int i, int sn) throws RemoteException
    {
        assert(isInValidState());
        assert(i < RN.length);
        assert(i != myId);
        Utils.debugMsg(myId, "Recibi un request de " + i + " con un sn de " + sn);
        RN[i] = Math.max(RN[i], sn);

        if(!isExcuting && hasToken) {
            try {
                Utils.debugMsg(myId, "Tengo el token y no lo estoy usando, se lo mandare a " + i + ".");
                if(reader.sendTokenTo(i, RN[i])) {
                    Utils.debugMsg(myId, i + " acepto el token.");
                    hasToken = false;
                } else {
                    Utils.debugMsg(myId, i + " no acepto el token, por lo tanto me lo quedo yo.");
                }
            } catch (RemoteException e) {
                System.err.println(e.toString());
                // e.printStackTrace(System.err);
                reader.killEveryone();
            }
        }
    }

    public void takeToken() throws RemoteException
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "Me acaba de llegar el token y lo acepte.");
        hasToken = true;
        isRequesting = false;
    }

    public void releaseCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        try {
            Utils.debugMsg(myId, "Gente! Ya no estoy usando el token!");
            if(hasToken && !isExcuting && reader.releaseCriticalSection(myId)) {
                Utils.debugMsg(myId, "Solte el token c:");
                hasToken = false;
            }
        } catch(RemoteException e) {
            System.err.println(e.toString());
            // e.printStackTrace(System.err);
            reader.killEveryone();
        }
    }

    public boolean canIExecuteTheCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        return hasToken;
    }

    public void startExecutingTheCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        assert(hasToken && !isExcuting);
        isExcuting = true;
    }

    public void finishTheExecutionOfTheCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        assert(hasToken && isExcuting);
        isExcuting = false;
    }

    public boolean amIExecutingTheCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        assert(isExcuting && hasToken || !isExcuting);
        return isExcuting;
    }

    public boolean shouldIKillMyself() throws RemoteException
    {
        assert(isInValidState());
        if(!everythingIsFine){
            return true;
        }
        return false;
    }

    public int getId() throws RemoteException
    {
        assert(isInValidState());
        return myId;
    }
}
