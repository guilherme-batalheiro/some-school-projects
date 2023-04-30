package pt.tecnico.distledger.server.exception;

public class CannotCreateBrokerException extends Exception {
    public CannotCreateBrokerException() {
        super("Cannot create broker account.");
    }
}
