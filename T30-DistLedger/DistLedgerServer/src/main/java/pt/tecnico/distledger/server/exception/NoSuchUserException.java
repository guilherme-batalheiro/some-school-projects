package pt.tecnico.distledger.server.exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException() {
        super("No such user.");
    }
}
