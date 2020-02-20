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

    public static final String ANSI_RESET               = "\u001B[0m";

    public static final String ANSI_BLACK               = "\u001B[30m";
    public static final String ANSI_RED                 = "\u001B[31m";
    public static final String ANSI_GREEN               = "\u001B[32m";
    public static final String ANSI_YELLOW              = "\u001B[33m";
    public static final String ANSI_BLUE                = "\u001B[34m";
    public static final String ANSI_PURPLE              = "\u001B[35m";
    public static final String ANSI_CYAN                = "\u001B[36m";
    public static final String ANSI_WHITE               = "\u001B[37m";

    public static final String ANSI_BACKGROUND_BLACK    = "\u001B[40m";
    public static final String ANSI_BACKGROUND_RED      = "\u001B[41m";
    public static final String ANSI_BACKGROUND_GREEN    = "\u001B[42m";
    public static final String ANSI_BACKGROUND_YELLOW   = "\u001B[43m";
    public static final String ANSI_BACKGROUND_BLUE     = "\u001B[44m";
    public static final String ANSI_BACKGROUND_PURPLE   = "\u001B[45m";
    public static final String ANSI_BACKGROUND_CYAN     = "\u001B[46m";
    public static final String ANSI_BACKGROUND_WHITE    = "\u001B[47m";

    public static void redPrintln(String msg)
    {
        System.out.println(ANSI_BACKGROUND_RED + msg + ANSI_RESET);
    }

    public static void yellowPrintln(String msg)
    {
        System.out.println(ANSI_BACKGROUND_YELLOW + msg + ANSI_RESET);
    }

    public static void greenPrintln(String msg)
    {
        System.out.println(ANSI_BACKGROUND_GREEN + msg + ANSI_RESET);
    }

    public static void bluePrintln(String msg)
    {
        System.out.println(ANSI_BACKGROUND_BLUE + msg + ANSI_RESET);
    }
}
