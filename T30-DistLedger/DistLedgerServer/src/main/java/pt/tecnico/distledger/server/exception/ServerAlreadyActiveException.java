package pt.tecnico.distledger.server.exception;

public class ServerAlreadyActiveException extends Exception {
    public ServerAlreadyActiveException() {
        super("Server is already active.");
    }
}
