import java.util.LinkedList;
import java.util.Queue;

public class Token {
	public Integer[] executed;
	public Queue<Integer> queue;

	public Token(Integer n) 
	{
		executed = new Integer[n];
		for(int i = 0; i < n; ++i){
			executed[i] = 0;
		}
		queue = new LinkedList<>();
	}

	public void request(Integer id)
	{
		this.queue.add(id);
	}

	public Integer getNextId()
	{
		return(this.queue.remove());
	}
}
