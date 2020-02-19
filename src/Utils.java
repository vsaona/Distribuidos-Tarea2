class Utils
{
    public static boolean debugEnabled = false;

    public static void debugMsg(String msg, boolean saltoDeLinea){
        if(debugEnabled){
            if(saltoDeLinea) {
                System.out.println(msg);
            } else {
                System.out.print(msg);
            }
        }
    }
    public static void debugMsg(String msg){
        debugMsg(msg, true);
    }

    public static void debugErr(String msg, boolean saltoDeLinea){
        if(debugEnabled){
            if(saltoDeLinea) {
                System.err.println(msg);
            } else {
                System.err.print(msg);
            }
        }
    }
    public static void debugErr(String msg){
        debugMsg(msg, true);
    }

    public static void sleep(long millis) throws InterruptedException
    {
        debugMsg("Dormire por: " + millis + "[ms]");
        Thread.sleep(millis);
        debugMsg("Desperte!\n");
    }
}