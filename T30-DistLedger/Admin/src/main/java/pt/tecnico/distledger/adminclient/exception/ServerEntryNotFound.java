package pt.tecnico.distledger.adminclient.exception;

public class ServerEntryNotFound extends Exception {
    public ServerEntryNotFound(String serverQualifier) {
        super("Server with qualifier " + serverQualifier + " not responding.");
    }
}
