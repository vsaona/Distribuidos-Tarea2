import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class CriticalSection
{
    private String fileName;
    private int capacity;
    private long milisecondsPerChar;
    private Site site = null;

    private boolean fileHasEnded;

    public CriticalSection(String fileName, int capacity, int speed) throws IOException
    {
        this.fileName = fileName;
        this.capacity = capacity;
        this.milisecondsPerChar = (long)(1000f/((double)speed));
        this.fileHasEnded = false;
        if(!(new File(fileName)).exists()) {
            throw new IOException("File '" + fileName + "' does not exists.");
        }
    }

    public void setSite(Site site)
    {
        assert(site != null);
        this.site = site;
    }

    public long getFileSize()
    {
        File f = new File(fileName);
        return f.length();
    }

    public String executeCriticalSection() throws IOException, InterruptedException 
    {
        assert(site != null);
        String charactersRead;

        site.startExecutingTheCriticalSection();

        assert(site.amIExecutingTheCriticalSection());
        Utils.debugMsg(site.getId(), "Empece la seccion critica");
        site.showState();
        try {
            charactersRead = criticalSection();
            Utils.colorPrintln(getFileSize(), site.getOriginalSize(), Utils.ANSI_WHITE +  "Extraje: " + Utils.ANSI_BLACK + charactersRead);
        } catch(IOException e) {
            // site.killEveryone();
            throw new IOException(e.getMessage());
        }
        site.finishTheExecutionOfTheCriticalSection();
        return charactersRead;
    }

    private String criticalSection() throws IOException, InterruptedException
    {
        int actualFilesize = (int)getFileSize();
        if(actualFilesize == 0) {
            this.fileHasEnded = true;
            return "";
        }

        char cbuff[] = new char[capacity];
        char tail[];
        if(capacity >= actualFilesize) {
            tail = new char[1];
            this.fileHasEnded = true;
        } else {
            tail = new char[actualFilesize-capacity];
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        reader.read(cbuff, 0, capacity);
        if(!this.fileHasEnded) {
            reader.read(tail);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        if(!this.fileHasEnded) {
            writer.write(tail);
        }
        writer.close();

        for(int i = 0; i < capacity; ++i) {
            Utils.debugMsg(site.getId(), i + "/" + capacity + ": " + (char)cbuff[i]);
            Utils.sleep(site.getId(), milisecondsPerChar);
        }
        return new String(cbuff);
    }

    public void waitMeIAmTired() throws InterruptedException 
    {
        Utils.debugMsg(site.getId(), "Me canse.");
        Utils.sleep(site.getId(), capacity*1000/2);
    }

    public boolean hasFileEnded()
    {
        return fileHasEnded;
    }
}
