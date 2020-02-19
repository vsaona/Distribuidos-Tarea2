import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.*;

public class Process
{
	public static final int rmiPort = 0xA1F;
	public static final String rmiUrl = "rmi://localhost:" + rmiPort + "/";
	public static final String rmiReaderUrl = rmiUrl + "tareaDeDistribuidos.exe";

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

	public static Reader getRemoteReader() throws InterruptedException, NotBoundException, MalformedURLException
	{
		Reader reader = null;
		while (reader == null) {
			Utils.sleep(1000);

			try {
				reader = (Reader)Naming.lookup(rmiReaderUrl);  
			} catch (RemoteException e){ // En caso de que este proceso haya sido invocado antes que el `bearer`.
				// e.printStackTrace(System.err);
				System.out.println("El bearer todavia no configura el RMI...");
			} catch (NotBoundException e){
				System.out.println("El objeto todavia no ha sido registrado en el RMI...");
			}
		}
		return reader;
	}

	public static void main(String[] args) throws InterruptedException, RemoteException, NotBoundException, MalformedURLException, IOException
	{
		Utils.debugEnabled = true;

		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		int delay = Integer.parseInt(args[4]);
		Utils.sleep(delay);

		Reader reader = setupRemoteMethod(args[5], processes, fileName, capacity, speed);
		if (reader == null) {
			reader = getRemoteReader();
		}

		try {
			SiteInterface site = reader.generateSite();
			System.out.println(site.getId());
			CriticalSection cs = new CriticalSection(fileName, capacity, speed);

			String charactersRead = "";
			while(!cs.hasFileEnded()) {
				if(site.shouldIKillMyself()) {
					Utils.debugMsg("Suicidaton!");
					break;
				}
				if(!site.didIRequestTheCriticalSection()) {
					Utils.debugMsg("Pidiendo seccion critica...");
					site.requestCriticalSection();
					Utils.debugMsg("Esperando...");
				}

				if(site.canIExecuteTheCriticalSection()) {
					Utils.debugMsg("Voy a empezar la seccion critica");
					site.startExecutingTheCriticalSection();
					Utils.debugMsg("Empece la seccion critica");
					String asdf = cs.executeCriticalSection();
					Utils.debugMsg("Termine la seccion critica");
					Utils.debugMsg("Lei: " + asdf);
					charactersRead += asdf;
					site.finishTheExecutionOfTheCriticalSection();

					Utils.debugMsg("Gente, ya termine!");
					site.releaseCriticalSection();

					if(cs.hasFileEnded()) {
						Utils.debugMsg("Suicidaton bailable!");
						reader.killEveryone();
					}

					Utils.debugMsg("\nMe canse.");
					cs.waitMeIAmTired();
				}

			}

			System.out.println(charactersRead);
		} catch(SocketException e) {
			Utils.debugMsg("SocketException: RMI se cayo.");
		} catch(ConnectException e) {
			Utils.debugMsg("ConnectException: RMI se cayo.");
		}
	}
}
