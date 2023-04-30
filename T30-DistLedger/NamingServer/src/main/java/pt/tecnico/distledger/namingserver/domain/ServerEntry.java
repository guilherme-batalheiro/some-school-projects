package pt.tecnico.distledger.namingserver.domain;

import pt.ulisboa.tecnico.distledger.contract.NamingServer;

public class ServerEntry {
    private final String serverHost;
    private final String serverPort;
    private final String serverQualifier;

    public ServerEntry(String serverHost, String serverPort, String serverQualifier) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverQualifier = serverQualifier;
    }

    public String getServerHost(){
        return this.serverHost;
    }

    public String getServerPort(){
        return this.serverPort;
    }

    public String getServerQualifier(){
        return this.serverQualifier;
    }

    public NamingServer.ServerEntry proto() {
        return NamingServer.ServerEntry.newBuilder()
                .setHost(serverHost)
                .setPort(serverPort)
                .setQualifier(serverQualifier)
                .build();
    }
}
