package pt.tecnico.distledger.server.exception;

public class ServerAlreadyInactiveException extends Exception {
    public ServerAlreadyInactiveException() {
        super("Server is already inactive.");
    }
}
