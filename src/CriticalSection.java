import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;

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

    public String executeCriticalSection() throws RemoteException, IOException, InterruptedException 
    {
        assert(site != null);
        String charactersRead;

        site.startExecutingTheCriticalSection();

        assert(site.amIExecutingTheCriticalSection());
        Utils.debugMsg(site.getId(), "Empece la seccion critica");
        site.showState();
        try {
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

                Utils.debugMsg(site.getId(), i + "/" + capacity + ": " + (char)c);
                characters[i++] = (char)c;

                Utils.sleep(site.getId(), milisecondsPerChar);
            }
            reader.close();
            updateFile();

            charactersRead = new String(characters);

        } catch(IOException e) {
            throw new IOException(e.getMessage());
        }
        site.finishTheExecutionOfTheCriticalSection();
        return charactersRead;
    }

    private void updateFile() throws IOException
    {
        try {
            int filesize = (int)getFileSize();
            if(filesize == 0) {
                return;
            }
            char characters[] = new char[filesize];
            Utils.debugMsg(site.getId(), "Borrando " + capacity + " caracteres del archivo (" + filesize + ").");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            reader.read(characters);
            reader.close();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            if(filesize - capacity > 0) {
                writer.write(characters, capacity, filesize - capacity);
            }
            writer.close();
        } catch(IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void waitMeIAmTired() throws InterruptedException 
    {
        Utils.debugMsg(site.getId(), "\nMe canse.");
        Utils.sleep(site.getId(), capacity*1000/2);
    }

    public boolean hasFileEnded()
    {
        return fileHasEnded;
    }
}
