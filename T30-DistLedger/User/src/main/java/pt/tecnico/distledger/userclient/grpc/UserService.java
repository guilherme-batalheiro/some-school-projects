package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.userclient.UserClientMain;
import pt.tecnico.distledger.userclient.VectorClock;
import pt.tecnico.distledger.userclient.exception.ServerEntryNotFound;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

public class UserService {
    private final ManagedChannel channel;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private final VectorClock prev;

    public UserService() {
        // Create naming server connection
        this.channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);

        this.prev = new VectorClock(2);
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

    public void updatePrevVectorClock(VectorClock n_v) {
        UserClientMain.debug("Received new vector clock: " + n_v + ".");
        UserClientMain.debug("Merge prev vector clock: " + prev + " with new vector clock: " + n_v + ".");
        prev.merge(n_v);
        UserClientMain.debug("Merge result: " + prev + ".");
    }

    public int balance(String serverQualifier, String account) throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);
        UserClientMain.debug("Connection to server " + server.getQualifier() + " created.");

        // Make a balance request.
        UserClientMain.debug("Make a balance request with vector clock: " + prev + ".");
        UserDistLedger.BalanceResponse response = tmpStub.balance(UserDistLedger.BalanceRequest.newBuilder()
                .setUserId(account)
                .setPrev(VectorClock.convertToUserDistLedgerVC(prev))
                .build());

        // Update the prev vector clock
        updatePrevVectorClock(VectorClock.convertToVectorClock(response.getNew()));

        int balance = response.getValue();
        tmpChannel.shutdownNow();

        return balance;
    }

    public void createAccount(String serverQualifier, String account)
            throws StatusRuntimeException, ServerEntryNotFound {
        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()
        ).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);

        UserClientMain.debug("Connection to server " + server.getQualifier() + " created.");

        // Make a create account request
        UserClientMain.debug("Make a create account request with the prev vector clock: " + prev + ".");
        UserDistLedger.CreateAccountResponse response = tmpStub.createAccount(UserDistLedger.CreateAccountRequest.newBuilder()
                .setUserId(account)
                .setPrev(VectorClock.convertToUserDistLedgerVC(prev))
                .build());

        // Update the prev vector clock
        updatePrevVectorClock(VectorClock.convertToVectorClock(response.getNew()));

        tmpChannel.shutdownNow();
    }

    public void transferTo(String serverQualifier, String fromAccount, String destAccount, int amount)
            throws StatusRuntimeException, ServerEntryNotFound {

        // Search for server.
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        // Create a connection to the server found.
        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);
        UserClientMain.debug("Connection to server " + server.getQualifier() + " created.");

        // Make a transfer to request.
        UserClientMain.debug("Make a transfer to request with vector clock: " + prev + ".");
        UserDistLedger.TransferToResponse response = tmpStub.transferTo(
                UserDistLedger.TransferToRequest.newBuilder()
                    .setAccountFrom(fromAccount)
                    .setAccountTo(destAccount)
                    .setAmount(amount)
                    .setPrev(VectorClock.convertToUserDistLedgerVC(prev))
                    .build()
        );

        // Update the prev vector clock
        updatePrevVectorClock(VectorClock.convertToVectorClock(response.getNew()));

        tmpChannel.shutdownNow();
    }

    public void shutDownService() {
        this.channel.shutdownNow();
    }

}
