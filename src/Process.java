import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;

public class Process
{

	public static final int rmiPort = 0xA1F;
	public static final String rmiUrl = "rmi://localhost:" + rmiPort;
	public static final String rmiReaderUrl = rmiUrl + java.lang.management.ManagementFactory.getRuntimeMXBean().getName();

	public static Reader setupRemoteMethod(String bearer, int processes, String fileName, int capacity, int speed) throws RemoteException, MalformedURLException
	{
		if (bearer.equalsIgnoreCase("True")) {
			Reader reader = new Extractor(processes, fileName, capacity, speed);
			LocateRegistry.createRegistry(rmiPort);
			Naming.rebind(rmiReaderUrl, reader);
			return reader;
		}
		return null;
	}

	public static Reader getRemoteReader() throws InterruptedException, RemoteException, MalformedURLException
	{
		Reader reader = null;
		while (reader == null) {
			Thread.sleep(1000); // Podría ser `delay`?

			try {
				reader = (Reader)Naming.lookup(rmiReaderUrl);  
			} catch (NotBoundException e){ // En caso de que este proceso haya sido invocado antes que el `bearer`.
				e.printStackTrace(System.err);
			}
		}

		return reader;
	}

	public static void main(String[] args) throws InterruptedException, RemoteException, MalformedURLException
	{
		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		int delay = Integer.parseInt(args[4]);
		Thread.sleep(delay);

		Reader reader = setupRemoteMethod(args[5], processes, fileName, capacity, speed);
		if (reader == null) {
			reader = getRemoteReader();
		}

		Site site = reader.generateSite();

		while(true) Thread.sleep(1000);
	}
}
