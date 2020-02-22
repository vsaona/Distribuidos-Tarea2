import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import javax.management.relation.InvalidRelationIdException;
import javax.naming.SizeLimitExceededException;

class Site extends UnicastRemoteObject implements SiteInterface
{
    private static final long serialVersionUID = 8575564069279616010L;

    private SitesHandler skHandler;
    private Token token = null;

    private int myId;
    private long originalSize;

    private boolean isExcuting = false;
    private boolean isRequesting = false;
    private boolean isWaiting = false;
    private boolean didExecute = false;

    private boolean everythingIsFine = true;

    public Site() throws RemoteException
    {
        super();
    }

    public Site(RMIStuff rmi, int processes, int myId, long originalSize) throws RemoteException, SizeLimitExceededException, InterruptedException, InvalidRelationIdException
    {
        super();
        if(myId >= processes) {
            throw new SizeLimitExceededException("Maximum amount of processes reached (" + processes + ").");
        }
        this.myId = myId;
        this.originalSize = originalSize;
        this.skHandler = new SitesHandler(this, rmi, processes, myId);

        if(myId == 0) {
            token = new Token(processes);
        }
    }

    public int getId()
    {
        checkValidState();
        return myId;
    }

    public int generateNewId() throws RemoteException, SizeLimitExceededException
    {
        checkValidState();
        return skHandler.generateNewId();
    }

    public long getOriginalSize() throws RemoteException
    {
        checkValidState();
        return originalSize;
    }

    public void registerMe(SiteInterface otherSite) throws RemoteException, SizeLimitExceededException, InvalidRelationIdException
    {
        checkValidState();
        skHandler.registerMe(otherSite);
    }

    public void showState()
    {
        checkValidState();
        if(!isExcuting && !isRequesting) {
            Utils.cyanPrintln(Utils.ANSI_WHITE + "Ocioso. RN: " + skHandler.rnAsStr());
        } else if(isRequesting) {
            Utils.purplePrintln(Utils.ANSI_WHITE + "Esperando token. RN: " + skHandler.rnAsStr());
        } else {
            Utils.whitePrintln(Utils.ANSI_BLACK + "Seccion critica. RN: " + skHandler.rnAsStr());
        }
    }

    boolean _isInValidState()
    {
        // Este metodo es feo. :c
        if(myId < 0) {
            return false;
        }
        if(token != null) {
            if(isExcuting) {
                if(isRequesting) {
                    return false;
                } else {
                    if(isWaiting) {
                        return false;
                    } else {
                        if(didExecute) {
                            return false;
                        } else {
                            return true;
                        }
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
                if(isRequesting) {
                    if(isWaiting) {
                        if(didExecute) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if(didExecute) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    if(isWaiting) {
                        return false;
                    } else {
                        if(didExecute) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
    }

    boolean isInValidState()
    {
        if(!_isInValidState()) {
            Utils.debugErr(myId, "Site invalid state:");
            Utils.debugErr(myId, "\thasToken: " + (token != null));
            Utils.debugErr(myId, "\tisExcuting: " + isExcuting);
            Utils.debugErr(myId, "\tisRequesting: " + isRequesting);
            Utils.debugErr(myId, "\tisWaiting: " + isWaiting);
            Utils.debugErr(myId, "\tdidExecute: " + didExecute);
            return false;
        }
        return true;
    }

    void checkValidState()
    {
        if(!isInValidState()) {
            throw new IllegalStateException("El estado del Site es invalido.");
        }
    }


    public boolean didIRequestTheToken()
    {
        checkValidState();
        return isRequesting || (token != null);
    }

    public void makeTokenRequest()
    {
        checkValidState();

        Utils.debugMsg(myId, "Pidiendo el token...");
        int sn = skHandler.incrementMyRN();

        if((token == null) && !isRequesting) {
            try {
                Utils.debugMsg(myId, "@everyone Quiero el token!");
                isRequesting = true;
                boolean didIGotTheToken = skHandler.requestEveryone(sn);
                if(!didIGotTheToken) {
                    waitToken();
                }
            } catch (RemoteException e) {
                System.err.println(e.toString());
                // e.printStackTrace(System.err);
                killEveryone();
            }
        }
    }

    // Returns true if this site had the token and gave it to i.
    public boolean request(int i, int sn) throws RemoteException
    {
        checkValidState();

        assert(i != myId);

        Utils.debugMsg(myId, "Recibi un request de " + i + " con un sn de " + sn);
        skHandler.updateRN(i, sn);
        showState();
        if((token != null) && !isExcuting && didExecute) {
            Utils.debugMsg(myId, "Tengo el token y no lo estoy usando.");
            if(skHandler.getRN(i) == token.getLN(i) + 1) {
                try {
                    Utils.debugMsg(myId, "A " + i + " le toca usar el token. Se lo mandare.");
                    skHandler.getSite(i).takeToken(removeToken(i));
                    return true;
                } catch (RemoteException e) {
                    // System.err.println(e.toString());
                    e.printStackTrace(System.err);
                    killEveryone();
                }
            } else {
                Utils.debugMsg(myId, "A " + i + " no le correspondia el token.");
            }
        }
        return false;
    }

    public void takeToken(Token token) throws RemoteException
    {
        checkValidState();

        Utils.debugMsg(myId, "Me acaba de llegar el token y lo acepte.");
        Utils.purplePrintln(Utils.ANSI_BLACK + "Acabo de recibir el token.");
        this.token = token;
        isRequesting = false;
        isWaiting = false;
        Utils.purplePrintln(Utils.ANSI_BLACK + "LN: " + token.lnAsStr());
        Utils.purplePrintln(Utils.ANSI_BLACK + "Queue: " + token.queueAsStr());
    }

    private Token removeToken(int j)
    {
        Utils.debugMsg(myId, "Entregando token a " + j + ".");
        Utils.purplePrintln(Utils.ANSI_BLACK + "Entregando token a " + j + ".");
        Utils.purplePrintln(Utils.ANSI_BLACK + "LN: " + token.lnAsStr());
        Utils.purplePrintln(Utils.ANSI_BLACK + "Queue: " + token.queueAsStr());
        Token tokenAux = this.token;
        this.token = null;
        return tokenAux;
    }

    public void waitToken() throws RemoteException
    {
        checkValidState();

        Utils.debugMsg(myId, "No me quieren pasar el token, asi que esperare.");
        isWaiting = true;
    }

    public boolean amIWaiting()
    {
        checkValidState();
        return isWaiting;
    }

    public void kill() throws RemoteException
    {
        Utils.debugMsg(myId, "Me asesinan. Ayuda :c.");
        everythingIsFine = false;
    }

    public void killEveryone()
    {
        Utils.debugMsg(myId, "He decidido matarlos a todos.");
        try {
            skHandler.killEveryone();
        } catch (RemoteException e) {
            Utils.debugErr(myId, "Ocurrio un error al intentar matarlos a todos.\n\tEsto es normal, probablemente ya se mataron todos y soy el que falta.");
            // Utils.debugErr(myId, e.toString());
        }
        Utils.debugMsg(myId, "Ahora cometere la autolesion mortal.");
        System.out.println("Ahora cometere la autolesion mortal.");
        System.exit(0);
    }

    public boolean shouldIKillMyself()
    {
        if(!everythingIsFine){
            return true;
        }
        return false;
    }


    public void releaseToken()
    {
        checkValidState();

        Utils.debugMsg(myId, "@everyone Ya no estoy usando el token!");
        if((token != null) && !isExcuting) {
            didExecute = false;

            token.setLN(myId, skHandler.getRN(myId));
            for(int j = 0; j < skHandler.usableLengthRN(); ++j) {
                if(skHandler.getRN(j) == token.getLN(j) + 1) {
                    token.addToQueue(j);
                }
            }

            if(token.isQueueEmpty()) {
                Utils.debugMsg(myId, "Nadie queria el token, asi que me lo quede.");
            } else {
                try {
                    int j = token.getNextId();
                    Utils.debugMsg(myId, j + " venia en la fila, por lo que le mandare el token c:.");
                    skHandler.getSite(j).takeToken(removeToken(j));
                    Utils.debugMsg(myId, "Ya no tengo el token.");
                } catch(RemoteException e) {
                    System.err.println(e.toString());
                    // e.printStackTrace(System.err);
                    killEveryone();
                }
            }
        } else {
            Utils.debugMsg(myId, "Me bugie.");
            killEveryone();
        }
    }


    public boolean canIExecuteTheCriticalSection()
    {
        checkValidState();

        return (token != null) && !didExecute;
    }

    public void startExecutingTheCriticalSection()
    {
        checkValidState();

        Utils.debugMsg(myId, "Voy a empezar la seccion critica");
        isExcuting = true;
    }

    public void finishTheExecutionOfTheCriticalSection()
    {
        checkValidState();

        Utils.debugMsg(myId, "Termine la seccion critica");
        isExcuting = false;
        didExecute = true;
    }
}
