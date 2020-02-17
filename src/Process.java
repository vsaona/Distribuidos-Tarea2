import java.rmi.*;
import java.rmi.registry.*;

public class Process
{
	public static void main(String[] args) throws InterruptedException
	{
		int processes = Integer.parseInt(args[0]);
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		Thread.sleep(Integer.parseInt(args[4]));
		boolean hasToken = false;
		if(args[5].equals("true") || args[5].equals("True")) {
			// createToken(Integer.parseInt(args[0]));
			hasToken = true;
			Token token = new Token(Integer.parseInt(args[0]));
		}
		try {
			Reader reader = new Extractor(processes, fileName, capacity, speed, hasToken);
			LocateRegistry.createRegistry(0xA1F);
			Naming.rebind("rmi://localhost:2591" + java.lang.management.ManagementFactory.getRuntimeMXBean().getName(), reader);
		} catch(Exception e) {
			System.out.println(e);
		}
		while(true) Thread.sleep(1000);
	}
}
