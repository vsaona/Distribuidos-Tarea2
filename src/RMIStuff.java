import java.rmi.*;
import java.rmi.registry.*;

public class RMIStuff
{
	public static int waitTime = 1000;
	public static final int rmiPort = 0xA1F;
	public static final String baseName = "tareaDeDistribuidos.exe/";

	Registry reg;

	public RMIStuff(boolean bearer) throws RemoteException, InterruptedException
	{
		if(bearer) {
			reg = createRegistry();
		} else {
			reg = getRegistry();
		}
	}

	public void setObject(int objId, Remote obj) throws RemoteException
	{
		try {
			Utils.debugMsg(-1, "Bindeando objecto en la posicion " + objId + ".");
			reg.rebind(baseName + objId, obj);
		} catch (RemoteException e) {
			throw new RemoteException(e.toString());
		}
	}
	
	public Remote getObject(int objId) throws RemoteException, InterruptedException
	{
		Remote obj = null;
		while(obj == null) {
			try {
				Utils.debugMsg(-1, "Buscando el objeto " + objId + ".");
				obj = reg.lookup(baseName + objId);
			} catch (NotBoundException e){
				System.out.println("El objeto todavia no ha sido registrado en el RMI...");
			}
			Utils.sleep(-1, waitTime);
		}
		return obj;
	}

	private static Registry createRegistry() throws RemoteException
	{
		Registry reg = null;
		try {
			Utils.debugMsg(-1, "Creando registro RMI en puerto " + rmiPort + ".");
			reg = LocateRegistry.createRegistry(rmiPort);
		} catch (RemoteException e) {
			throw new RemoteException(e.toString());
		}
		return reg;
	}

	private static Registry getRegistry() throws RemoteException, InterruptedException
	{
		Registry reg = null;
		while(reg == null) {
			try {
				Utils.debugMsg(-1, "Buscando registro.");
				reg = LocateRegistry.getRegistry(rmiPort);
			} catch (RemoteException e){ // En caso de que este proceso haya sido invocado antes que el `bearer`.
				System.out.println("El bearer todavia no configura el RMI...");
			}
			Utils.sleep(-1, waitTime);
		}
		return reg;
	}
}