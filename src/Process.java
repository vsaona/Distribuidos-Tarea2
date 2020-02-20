import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

import javax.naming.SizeLimitExceededException;

public class Process
{
	public static void colorPrintln(long remainingSize, long originalSize, String msg)
	{
		long percentage = 0;
		if(originalSize != 0) {
			percentage = 100*remainingSize/originalSize;
		}
		if(percentage < 25) {
			Utils.redPrintln(msg);
		} else if(percentage < 50) {
			Utils.yellowPrintln(msg);
		} else if(percentage < 75) {
			Utils.greenPrintln(msg);
		} else {
			Utils.bluePrintln(msg);
		}
	}

	public static void main(String[] args) throws InterruptedException, RemoteException, MalformedURLException, IOException, SizeLimitExceededException
	{
		Utils.enableDebug = true;

		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		int delay = Integer.parseInt(args[4]);
		if(delay > 0) {
			System.out.println("Delay: " + delay + "[ms]");
			Utils.sleep(-1, delay);
		}

		CriticalSection cs = new CriticalSection(fileName, capacity, speed);
		Reader reader = RMIStuff.setupRemoteMethod(args[5], processes, cs.getFileSize());
		if(reader == null) {
			reader = RMIStuff.getRemoteReader();
		}

		int myId = -1;
		String totalcharactersRead = "";
		try {
			Site site = new Site(reader, processes);
			reader.registerSite(site);
			cs.setSite(site);
			myId = site.getId();
			System.out.println("Mi id es: " + myId);
			site.showState();

			while(!cs.hasFileEnded()) {
				if(site.shouldIKillMyself()) {
					Utils.debugMsg(-1, "Suicidaton!");
					System.out.println("No quedan caracteres en el archivo.");
					break;
				}

				if(site.amIWaiting()) {
					Thread.sleep(50);
				}

				if(!site.didIRequestTheCriticalSection()) {
					site.requestCriticalSection();
					site.showState();
				}

				if(site.canIExecuteTheCriticalSection()) {
					String charactersRead = cs.executeCriticalSection();

					colorPrintln(cs.getFileSize(), reader.originalSize(), Utils.ANSI_WHITE +  "Extracted: " + Utils.ANSI_BLACK + charactersRead);
					totalcharactersRead += charactersRead;

					if(cs.hasFileEnded()) {
						Utils.debugMsg(site.getId(), "Suicidaton bailable!");
						reader.killEveryone();
						break;
					}

					site.releaseCriticalSection();

					site.showState();
					cs.waitMeIAmTired();
				}

			}
		} catch(SocketException e) {
			Utils.debugErr(myId, "SocketException: RMI se cayo.");
		} catch(ConnectException e) {
			Utils.debugErr(myId, "ConnectException: RMI se cayo.");
		} catch(UnmarshalException e) {
			Utils.debugErr(myId, "UnmarshalException: RMI se cayo.");
		}

		colorPrintln(cs.getFileSize(), reader.originalSize(), Utils.ANSI_WHITE +  "Total extracted: " + Utils.ANSI_BLACK + totalcharactersRead);

		System.out.println("Terminando el proceso.");
		System.exit(0);
	}
}
