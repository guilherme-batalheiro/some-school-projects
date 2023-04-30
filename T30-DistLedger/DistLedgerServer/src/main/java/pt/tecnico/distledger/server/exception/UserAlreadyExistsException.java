package pt.tecnico.distledger.server.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super("User already exists.");
    }
}
