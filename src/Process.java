
public class Process {

	static boolean hasToken = false;
	static Token token = null;

	private void createToken(Integer n) {
		this.hasToken = true;
		this.token = new Token(n);
	}

	public static void main(String[] args) throws InterruptedException
	{
		String fileName = args[1];
		int capacity = Integer.parseInt(args[2]);
		int speed = Integer.parseInt(args[3]);
		Thread.sleep(Integer.parseInt(args[4]));
		if(args[5].equals("true") || args[5].equals("True")) {
			// createToken(Integer.parseInt(args[0]));
			hasToken = true;
			token = new Token(Integer.parseInt(args[0]));
		}
		
	}
}
