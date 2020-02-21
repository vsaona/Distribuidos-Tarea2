import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Token implements Serializable
{
	private static final long serialVersionUID = -6085235567096462778L;

	private int[] LN;
	private Queue<Integer> queue;

	public Token(int n) 
	{
		LN = new int[n];
		for(int i = 0; i < n; ++i){
			LN[i] = 0;
		}
		queue = new LinkedList<>();
	}

	public int getLN(int i)
	{
		assert(i < LN.length);
		return LN[i];
	}

	public void setLN(int i, int sn)
	{
		assert(i < LN.length);
		LN[i] = sn;
	}

	public void addToQueue(int id)
	{
		if(!queue.contains(id)) {
			queue.add(id);
		}
	}

	public boolean isQueueEmpty()
	{
		return queue.isEmpty();
	}

	public int getNextId()
	{
		return this.queue.remove();
	}

	public String lnAsStr()
	{
		return Utils.arrayToStr(LN);
	}

	public String queueAsStr()
	{
		String str = "<";
		boolean first = true;
		for(int val: queue) {
			if(first) {
				first = false;
			} else {
				str += ", ";
			}
			str += val;
		}
		str += ">";
		return str;
	}
}
