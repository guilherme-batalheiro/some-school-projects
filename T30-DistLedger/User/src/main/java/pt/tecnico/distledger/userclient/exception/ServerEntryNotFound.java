package pt.tecnico.distledger.userclient.exception;

public class ServerEntryNotFound extends Exception {
    public ServerEntryNotFound(String serverQualifier) {
        super("Server with qualifier " + serverQualifier + " not responding.");
    }
}
