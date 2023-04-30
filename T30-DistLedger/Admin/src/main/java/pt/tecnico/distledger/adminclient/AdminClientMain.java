package pt.tecnico.distledger.adminclient;

public class AdminClientMain {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
    public static void debug(String debugMessage) {
        if (DEBUG_FLAG) System.err.println("[\u001B[1m\u001B[96mDEBUG\u001B[0m] " + debugMessage);
    }
    public static void main(String[] args) {
        
        System.out.println(AdminClientMain.class.getSimpleName());

        CommandParser parser = new CommandParser();
        parser.parseInput();

    }
}
