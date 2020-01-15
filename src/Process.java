
public class Process extends Thread{

	public static void main(String[] args)
	{
		int childQuantity = Integer.parseInt(args[0]);
		for(int i = 0; i < childQuantity; i++) {
			Process child = new Process();
			child.start();
		}
		return;
	}

	public void run()
	{
		System.out.println("Hola");
		return;
	}

}
