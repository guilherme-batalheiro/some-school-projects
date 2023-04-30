package pt.tecnico.distledger.server.exception;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds.");
    }
}