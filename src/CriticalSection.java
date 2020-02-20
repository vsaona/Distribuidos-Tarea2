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
    private SiteInterface site = null;

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

    public void setSite(SiteInterface site)
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
        assert(site.amIExecutingTheCriticalSection());

        Utils.debugMsg(-1, "Entre al metodo de la seccion critica.");
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
                Utils.debugMsg(-1, i + "/" + capacity + ": " + (char)c);
                characters[i++] = (char)c;
                Utils.sleep(-1, milisecondsPerChar);
            }
            reader.close();
            updateFile();
            Utils.debugMsg(-1, "Estoy saliendo del metodo de la seccion critica.\n");
            return new String(characters);
        } catch(IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void updateFile() throws IOException
    {
        try {
            int filesize = (int)getFileSize();
            if(filesize == 0) {
                return;
            }
            char characters[] = new char[filesize];
            Utils.debugMsg(-1, "Borrando " + capacity + " caracteres del archivo (" + filesize + ").");

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            reader.read(characters);
            reader.close();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            writer.write(characters, capacity, Math.max(filesize - capacity, 0));
            writer.close();
        } catch(IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void waitMeIAmTired() throws InterruptedException 
    {
        Utils.sleep(-1, capacity*1000/2);
    }

    public boolean hasFileEnded()
    {
        return fileHasEnded;
    }
}
