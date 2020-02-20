class Utils
{
    public static boolean enableDebug = false;
    public static boolean enableSleepMsgDebug = false;

    public static void debugMsg(int processId, String msg, boolean saltoDeLinea)
    {
        if(enableDebug){
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
        if(enableDebug){
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
            if(enableSleepMsgDebug) {
                debugMsg(processId, "Dormire por: " + millis + "[ms]");
            }
            Thread.sleep(millis);
            if(enableSleepMsgDebug) {
                debugMsg(processId, "Desperte!\n");
            }
        } catch (InterruptedException e) {
            throw new InterruptedException("A problem has ocurred when trying to sleep.\n\tprocessId: " + processId + "\n" + e.toString());
        }
    }
}