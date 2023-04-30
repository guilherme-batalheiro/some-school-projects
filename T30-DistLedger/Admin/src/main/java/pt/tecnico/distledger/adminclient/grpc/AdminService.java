package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.adminclient.AdminClientMain;
import pt.tecnico.distledger.adminclient.exception.ServerEntryNotFound;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;


public class AdminService {

    private final ManagedChannel channel;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    public AdminService() {
        // Create naming server connection
        this.channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    public NamingServer.ServerEntry searchForServer(String serverQualifier)
            throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.LookupResponse response = this.stub.lookup(NamingServer.LookupRequest.newBuilder()
                .setServiceName("DistLedger")
                .setServerQualifier(serverQualifier)
                .build());

        if (response.getRetrievedServersCount() == 0)
            throw new ServerEntryNotFound(serverQualifier);

        return response.getRetrievedServers(0);
    }

    public void activate(String serverQualifier) throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() +
                ":" + server.getPort()).usePlaintext().build();

        AdminServiceGrpc.AdminServiceBlockingStub tmpStub = AdminServiceGrpc.newBlockingStub(tmpChannel);
        AdminClientMain.debug("Connection to the server created.");

        // Make a activate request.
        tmpStub.activate(ActivateRequest.newBuilder().build());
        tmpChannel.shutdownNow();
    }

    public void deactivate(String serverQualifier) throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() +
                        ":" + server.getPort()).usePlaintext().build();

        AdminServiceGrpc.AdminServiceBlockingStub tmpStub = AdminServiceGrpc.newBlockingStub(tmpChannel);
        AdminClientMain.debug("Connection to the server " + server.getQualifier() + " created.");

        // Make a deactivate request.
        tmpStub.deactivate(DeactivateRequest.newBuilder().build());
        tmpChannel.shutdownNow();
    }

    public void gossip(String serverQualifier) throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() +
                        ":" + server.getPort()).usePlaintext().build();

        AdminServiceGrpc.AdminServiceBlockingStub tmpStub = AdminServiceGrpc.newBlockingStub(tmpChannel);
        AdminClientMain.debug("Connection to the server " + server.getQualifier() + " created.");

        // Make a gossip request.
        tmpStub.gossip(GossipRequest.newBuilder().build());
        tmpChannel.shutdownNow();
    }

    public LedgerState getLedgerState(String serverQualifier) throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() +
                        ":" + server.getPort()).usePlaintext().build();

        AdminServiceGrpc.AdminServiceBlockingStub tmpStub = AdminServiceGrpc.newBlockingStub(tmpChannel);
        AdminClientMain.debug("Connection to the server " + server.getQualifier() + " created.");

        // Make a get ledger state request.
        LedgerState ls = tmpStub.getLedgerState(getLedgerStateRequest.newBuilder().build()).getLedgerState();
        tmpChannel.shutdownNow();

        return ls;
    }

    public void shutDownService() {
        this.channel.shutdownNow();
    }
}
