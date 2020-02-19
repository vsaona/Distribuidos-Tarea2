import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

class CriticalSection {
    private String fileName;
    private int capacity;
    private long milisecondsPerChar;

    private boolean fileHasEnded;

    public CriticalSection(String fileName, int capacity, int speed) 
    {
        this.fileName = fileName;
        this.capacity = capacity;
        this.milisecondsPerChar = (long)(1000f/((double)speed));
        this.fileHasEnded = false;
    }

    public String executeCriticalSection() throws IOException, InterruptedException 
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        int c;
        int i = 0;
        char characters[] = new char[capacity];
        while(i < capacity) {
            c = reader.read();
            if(c == -1){
                this.fileHasEnded = true;
                break;
            }
            characters[i] = (char)c;
            Thread.sleep(milisecondsPerChar);
        }
        reader.close();
        return new String(characters);
    }

    public void waitMeIAmTired() throws InterruptedException 
    {
        Thread.sleep(capacity*1000/2);
    }

    public boolean hasFileEnded()
    {
        return fileHasEnded;
    }
}