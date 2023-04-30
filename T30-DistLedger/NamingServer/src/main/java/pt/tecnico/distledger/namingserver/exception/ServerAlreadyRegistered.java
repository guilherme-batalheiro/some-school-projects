package pt.tecnico.distledger.namingserver.exception;

public class ServerAlreadyRegistered extends Exception {
    public ServerAlreadyRegistered() {
        super("Server already registered.");
    }
}
