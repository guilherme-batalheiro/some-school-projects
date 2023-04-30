package pt.tecnico.distledger.server.exception;

public class BalanceNotZeroException extends Exception {
    public BalanceNotZeroException() {
        super("Balance not zero.");
    }
}
