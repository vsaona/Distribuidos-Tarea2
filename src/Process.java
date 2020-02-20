import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;

public class Process
{
	public static void main(String[] args) throws InterruptedException, RemoteException, MalformedURLException, IOException
	{
		Utils.debugEnabled = true;

		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		int delay = Integer.parseInt(args[4]);
		Utils.sleep(-1, delay);

		CriticalSection cs = new CriticalSection(fileName, capacity, speed);
		Reader reader = RMIStuff.setupRemoteMethod(args[5], processes, cs.getFileSize());
		if(reader == null) {
			reader = RMIStuff.getRemoteReader();
		}

		try {
			SiteInterface site = reader.generateSite();
			cs.setSite(site);
			System.out.println("Mi id es: " + site.getId());

			String charactersRead = "";
			while(!cs.hasFileEnded()) {
				if(site.shouldIKillMyself()) {
					Utils.debugMsg(-1, "Suicidaton!");
					break;
				}
				if(!site.didIRequestTheCriticalSection()) {
					Utils.debugMsg(-1, "Pidiendo seccion critica...");
					site.requestCriticalSection();
					Utils.debugMsg(-1, "Esperando...");
				}

				if(site.canIExecuteTheCriticalSection()) {
					Utils.debugMsg(-1, "Voy a empezar la seccion critica");
					site.startExecutingTheCriticalSection();
					Utils.debugMsg(-1, "Empece la seccion critica");
					String asdf = cs.executeCriticalSection();
					Utils.debugMsg(-1, "Termine la seccion critica");
					Utils.debugMsg(-1, "Lei: " + asdf);
					charactersRead += asdf;
					site.finishTheExecutionOfTheCriticalSection();

					Utils.debugMsg(-1, "Gente, ya termine!");
					site.releaseCriticalSection();

					if(cs.hasFileEnded()) {
						Utils.debugMsg(-1, "Suicidaton bailable!");
						reader.killEveryone();
					}

					Utils.debugMsg(-1, "\nMe canse.");
					cs.waitMeIAmTired();
				}

			}

			System.out.println(charactersRead);
		} catch(SocketException e) {
			Utils.debugErr(-1, "SocketException: RMI se cayo.");
		} catch(ConnectException e) {
			Utils.debugErr(-1, "ConnectException: RMI se cayo.");
		}
		Utils.debugMsg(-1, "Terminando el programa.");
	}
}
