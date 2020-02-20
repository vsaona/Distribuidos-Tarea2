class Utils
{
    public static boolean debugEnabled = false;

    public static void debugMsg(int processId, String msg, boolean saltoDeLinea)
    {
        if(debugEnabled){
            if(saltoDeLinea) {
                System.out.println("(" + processId + ") " + msg);
            } else {
                System.out.print("(" + processId + ") " + msg);
            }
        }
    }
    public static void debugMsg(int processId, String msg)
    {
        debugMsg(processId, msg, true);
    }

    public static void debugErr(int processId, String msg, boolean saltoDeLinea)
    {
        if(debugEnabled){
            if(saltoDeLinea) {
                System.err.println("(" + processId + ") " + msg);
            } else {
                System.err.print("(" + processId + ") " + msg);
            }
        }
    }
    public static void debugErr(int processId, String msg)
    {
        debugMsg(processId, msg, true);
    }

    public static void sleep(int processId, long millis) throws InterruptedException
    {
        try {
            debugMsg(processId, "Dormire por: " + millis + "[ms]");
            Thread.sleep(millis);
            debugMsg(processId, "Desperte!\n");
        } catch (InterruptedException e) {
            throw new InterruptedException("A problem has ocurred when trying to sleep.\n\tprocessId: " + processId + "\n" + e.toString());
        }
    }
}