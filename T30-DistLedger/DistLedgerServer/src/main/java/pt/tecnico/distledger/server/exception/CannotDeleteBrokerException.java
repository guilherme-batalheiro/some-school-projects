package pt.tecnico.distledger.server.exception;

public class CannotDeleteBrokerException extends Exception {
    public CannotDeleteBrokerException() {
        super("Cannot delete broker account.");
    }
}
