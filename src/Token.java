import java.util.LinkedList;
import java.util.Queue;

public class Token {
	public Integer[] executed;
	public Queue<Integer> queue;

	public Token(Integer n) 
	{
		executed = new Integer[n];
		queue = new LinkedList<>();
	}

	public void waitToken(Integer id)
	{
		this.queue.add(id);
	}

	public Integer getNextId()
	{
		return(this.queue.remove());
	}
}
