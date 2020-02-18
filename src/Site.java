import java.rmi.RemoteException;
import java.lang.Math;

class Site
{

    private Reader reader;
    private int myId;
    private Integer[] RN;
    private boolean hasToken = false;
    private boolean isExcuting = false;
    private boolean isRequesting = false;

    public Site(Reader reader, int procesess, int myId)
    {
        this.reader = reader;
        this.myId = myId;
        assert(myId < procesess);
        this.RN = new Integer[procesess];
        for(int i = 0; i < procesess; ++i){
            this.RN[i] = 0;
        }
    }

    public void requestCriticalSection()
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

    public void receiveExternalRequest(int i, int sn)
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

	public void takeToken(){
        hasToken = true;
    }

    public void releaseCriticalSection(){
        try {
            if(hasToken && reader.releaseCriticalSection(myId, RN)) {
                hasToken = false;
            }
        } catch(RemoteException e) {
            e.printStackTrace(System.err);
        }
    }
}