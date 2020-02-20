import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;

public class RMIStuff
{
	public static final int rmiPort = 0xA1F;
	public static final String rmiUrl = "rmi://localhost:" + rmiPort + "/";
	public static final String rmiReaderUrl = rmiUrl + "tareaDeDistribuidos.exe";

	public static Reader setupRemoteMethod(String bearer, int processes, long filesize) throws RemoteException, MalformedURLException
	{
		if(bearer.equalsIgnoreCase("True")) {
			try {
				Reader reader = new Extractor(processes, filesize);
				Utils.debugMsg(-1, "Creando registro RMI en puerto " + rmiPort + ".");
				LocateRegistry.createRegistry(rmiPort);
				Utils.debugMsg(-1, "Bindeando objecto.");
				Naming.rebind(rmiReaderUrl, reader);
				return reader;
			} catch (RemoteException e) {
				throw new RemoteException(e.toString());
			}
		}
		return null;
	}

	public static Reader getRemoteReader() throws InterruptedException, MalformedURLException
	{
		Reader reader = null;
		while(reader == null) {
			try {
				Utils.debugMsg(-1, "Buscando objeto.");
				reader = (Reader)Naming.lookup(rmiReaderUrl);  
			} catch (RemoteException e){ // En caso de que este proceso haya sido invocado antes que el `bearer`.
				System.out.println("El bearer todavia no configura el RMI...");
			} catch (NotBoundException e){
				System.out.println("El objeto todavia no ha sido registrado en el RMI...");
			}
			Utils.sleep(-1, 1000);
		}
		return reader;
	}
}