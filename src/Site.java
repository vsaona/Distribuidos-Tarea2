import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.SizeLimitExceededException;

import java.lang.Math;

class Site extends UnicastRemoteObject implements SiteInterface
{
    private static final long serialVersionUID = 8575564069279616010L;

    private Reader reader;
    private int processes;
    private int myId = -1;
    private Integer[] RN;
    private boolean hasToken = false;
    private boolean isExcuting = false;
    private boolean isRequesting = false;
    private boolean isWaiting = false;

    long originalSize;

    boolean everythingIsFine = true;

    public Site() throws RemoteException
	{
		super();
    }

    public Site(Reader reader, int procesess) throws RemoteException
    {
        super();
        assert(reader != null);
        this.reader = reader;
        this.processes = procesess;
        this.RN = new Integer[procesess];
        for(int i = 0; i < procesess; ++i){
            this.RN[i] = 0;
        }
        this.originalSize = reader.originalSize();
    }

    public void setId(int id, int bearerProcesess) throws RemoteException, RuntimeException, SizeLimitExceededException
    {
        if(myId >= 0) {
            throw new RuntimeException("Can't re-set Site's id.");
        }
        if(id >= processes) {
			throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        if(processes != bearerProcesess) {
            throw new RuntimeException("Processes amount mismatch.");
        }
        this.myId = id;
    }

    public int getId()
    {
        assert(isInValidState());
        return myId;
    }

    public void showState()
    {
        String RNstr = "[(0, " + RN[0] + ")";
        for(int i = 1; i < RN.length; ++i) {
            RNstr += ", (" + i + ", " + RN[i] + ")";
        }
        RNstr += "]";
        if(!isExcuting && !isRequesting) {
            Utils.cyanPrintln(Utils.ANSI_WHITE + "Ocioso. " + RNstr);
        } else if(isRequesting) {
            Utils.purplePrintln(Utils.ANSI_WHITE + "Esperando token. " + RNstr);
        } else {
            Utils.whitePrintln(Utils.ANSI_BLACK + "Seccion critica. " + RNstr);
        }
    }

    public long getOriginalSize()
    {
        return originalSize;
    }

    boolean _isInValidState()
    {
        if(myId < 0) {
            return false;
        }
        if(hasToken) {
            if(isExcuting) {
                if(isRequesting) {
                    return false;
                } else {
                    if(isWaiting) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                if(isRequesting) {
                    return false;
                } else {
                    if(isWaiting) {
                        return false;
                    } else {
                        return true;
                    }
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
        assert(id < processes);
        return RN[id];
    }

    public boolean didIRequestTheCriticalSection()
    {
        assert(isInValidState());
        return isRequesting || hasToken;
    }

    // change to `requestToken()` (?)
    public void requestCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "Pidiendo seccion critica...");
        if(!hasToken && !isRequesting) {
            try {
                Utils.debugMsg(myId, "@everyone Quiero el token!");
                RN[myId] = RN[myId] + 1;
                isRequesting = true;
                reader.request(myId, RN[myId]);
            } catch (RemoteException e) {
                System.err.println(e.toString());
                reader.killEveryone();
            }
        }
    }

    public boolean receiveExternalRequest(int i, int sn) throws RemoteException
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
                    return true;
                } else {
                    Utils.debugMsg(myId, i + " no acepto el token, por lo tanto me lo quedo yo.");
                }
            } catch (RemoteException e) {
                System.err.println(e.toString());
                reader.killEveryone();
            }
        }
        return false;
    }

    public void takeToken() throws RemoteException
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "Me acaba de llegar el token y lo acepte.");
        hasToken = true;
        isRequesting = false;
        isWaiting = false;
    }

    public void waitToken() throws RemoteException
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "No me quieren pasar el token, asi que esperare.");
        isWaiting = true;
    }

    public void kill() throws RemoteException
    {
        everythingIsFine = false;
    }

    public void releaseCriticalSection() throws RemoteException
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "@everyone Ya no estoy usando el token!");
        if(hasToken && !isExcuting) {
            try {
                if(reader.releaseCriticalSection(myId)) {
                    Utils.debugMsg(myId, "Solte el token c:");
                    hasToken = false;
                } else {
                    Utils.debugMsg(myId, "Nadie queria el token, asi que me lo quede.");
                }
            } catch(RemoteException e) {
                System.err.println(e.toString());
                reader.killEveryone();
            }
        } else {
            Utils.debugMsg(myId, "Me bugie.");
            reader.killEveryone();
        }
    }

    public boolean amIWaiting()
    {
        assert(isInValidState());
        return isWaiting;
    }

    public boolean canIExecuteTheCriticalSection()
    {
        assert(isInValidState());
        return hasToken;
    }

    public void startExecutingTheCriticalSection()
    {
        assert(isInValidState());
        assert(hasToken && !isExcuting);
        Utils.debugMsg(myId, "Voy a empezar la seccion critica");
        isExcuting = true;
    }

    public void finishTheExecutionOfTheCriticalSection()
    {
        assert(isInValidState());
        Utils.debugMsg(myId, "Termine la seccion critica");
        isExcuting = false;
    }

    public boolean amIExecutingTheCriticalSection()
    {
        assert(isInValidState());
        return isExcuting;
    }

    public boolean shouldIKillMyself()
    {
        assert(isInValidState());
        if(!everythingIsFine){
            return true;
        }
        return false;
    }
}
