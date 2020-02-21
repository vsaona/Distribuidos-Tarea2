import java.rmi.*;

import javax.management.relation.InvalidRelationIdException;
import javax.naming.SizeLimitExceededException;

public interface SiteInterface extends Remote
{
    public int getId() throws RemoteException;
    public int generateNewId() throws RemoteException, SizeLimitExceededException;
    public long getOriginalSize() throws RemoteException;
    public void registerMe(SiteInterface otherSite) throws RemoteException, SizeLimitExceededException, InvalidRelationIdException;

    // Returns true if this site had the token and gave it to i.
    public boolean request(int i, int sn) throws RemoteException;
    public void takeToken(Token token) throws RemoteException;
    public void waitToken() throws RemoteException;
    public void kill() throws RemoteException;
}
