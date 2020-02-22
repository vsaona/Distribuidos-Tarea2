import java.io.IOException;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.UnmarshalException;

public class Process
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(args.length > 6 && args[6].equalsIgnoreCase("easterEgg")) {
			Utils.enableDebug = true;
		}

		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		int delay = Integer.parseInt(args[4]);
		boolean bearer = args[5].equalsIgnoreCase("True");
		if(delay > 0) {
			System.out.println("Delay: " + delay + "[ms]");
			Utils.sleep(-1, delay);
		}

		int myId = -1;
		CriticalSection cs = new CriticalSection(fileName, capacity, speed);
		RMIStuff rmi = null;
		Site site = null;
		try {
			rmi = new RMIStuff(bearer);
			if(bearer) {
				myId = 0;
				site = new Site(rmi, processes, myId, cs.getFileSize());
			} else {
				SiteInterface site0 = (SiteInterface)rmi.getObject(0);
				myId = site0.generateNewId();
				site = new Site(rmi, processes, myId, site0.getOriginalSize());
			}
			rmi.setObject(myId, site);
			cs.setSite(site);
			System.out.println("Mi id es: " + myId);
		} catch(Exception e) {
			System.err.println(e.toString());
			// e.printStackTrace(System.err);
			System.exit(-1);
		}

		String totalcharactersRead = "";
		try {
			site.showState();

			while(!cs.hasFileEnded()) {
				if(site.shouldIKillMyself()) {
					Utils.debugMsg(myId, "Me mori :s");
					break;
				}

				if(site.amIWaiting()) {
					Thread.sleep(50);
				}

				if(!site.didIRequestTheToken()) {
					site.makeTokenRequest();
					site.showState();
				}

				if(site.canIExecuteTheCriticalSection()) {
					totalcharactersRead += cs.executeCriticalSection();

					if(cs.hasFileEnded()) {
						Utils.debugMsg(site.getId(), "Suicidaton bailable!");
						site.killEveryone();
						break;
					}

					site.releaseToken();

					site.showState();
					cs.waitMeIAmTired();
				}
			}

			System.out.println("No quedan caracteres en el archivo.");

		} catch(SocketException e) {
			System.err.println(e.toString());
			System.err.println("SocketException: RMI se cayo.");
		} catch(ConnectException e) {
			System.err.println(e.toString());
			System.err.println("ConnectException: RMI se cayo.");
		} catch(UnmarshalException e) {
			System.err.println(e.toString());
			System.err.println("UnmarshalException: RMI se cayo.");
		} catch(Exception e) {
			// Capturamos todas las excepciones para que el programa pueda finalizar.
			System.err.println(e.toString());
			// e.printStackTrace(System.err);
		}

		Utils.cyanPrintln(Utils.ANSI_WHITE +  "Total de caracteres extraidos: " + Utils.ANSI_BLACK + totalcharactersRead);
		Utils.cyanPrintln(Utils.ANSI_WHITE +  "Extraje " + totalcharactersRead.length() + Utils.ANSI_BLACK + " caracteres.");

		System.out.println("Terminando el proceso.");
		site.killEveryone();
		System.exit(0);
	}
}
