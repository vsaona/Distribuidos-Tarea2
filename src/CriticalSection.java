import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

class CriticalSection
{
    private String fileName;
    private int capacity;
    private long milisecondsPerChar;
    private SiteInterface site = null;

    private boolean fileHasEnded;

    public CriticalSection(String fileName, int capacity, int speed) 
    {
        this.fileName = fileName;
        this.capacity = capacity;
        this.milisecondsPerChar = (long)(1000f/((double)speed));
        this.fileHasEnded = false;
    }

    public void setSite(SiteInterface site)
    {
        assert(site != null);
        this.site = site;
    }

    public String executeCriticalSection() throws IOException, InterruptedException 
    {
        assert(site != null);
        assert(site.amIExecutingTheCriticalSection());

        Utils.debugMsg("Entre al metodo de la seccion critica.");
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
            Utils.debugMsg(i + "/" + capacity + ": " + (char)c);
            characters[i++] = (char)c;
            Utils.sleep(milisecondsPerChar);
        }
        reader.close();
        Utils.debugMsg("Estoy saliendo del metodo de la seccion critica.\n");
        return new String(characters);
    }

    public void waitMeIAmTired() throws InterruptedException 
    {
        Utils.sleep(capacity*1000/2);
    }

    public boolean hasFileEnded()
    {
        return fileHasEnded;
    }
}
