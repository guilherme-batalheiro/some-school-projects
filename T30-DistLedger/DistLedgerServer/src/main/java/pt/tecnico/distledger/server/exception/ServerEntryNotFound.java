package pt.tecnico.distledger.server.exception;

public class ServerEntryNotFound extends Exception {
    public ServerEntryNotFound(String serverQualifier) {
        super("No server entry with the qualifier " + serverQualifier + " found.");
    }
}
