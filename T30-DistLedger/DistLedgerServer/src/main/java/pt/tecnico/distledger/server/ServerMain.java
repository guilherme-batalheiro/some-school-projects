package pt.tecnico.distledger.server;

import io.grpc.*;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        // check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            return;
        }

        final int port = Integer.parseInt(args[0]);
        final String serverQualifier = args[1];

        ServerState serverState = new ServerState(2, serverQualifier);

        final BindableService userImpl = new UserServiceImpl(serverState);
        final BindableService adminImpl = new AdminServiceImpl(serverState);
        final BindableService crossServerImpl = new CrossServerServiceImpl(serverState);

        // Create a new server to listen on port
        Server server = ServerBuilder.forPort(port)
                .addService(userImpl)
                .addService(adminImpl)
                .addService(crossServerImpl).build();

        // Start the server
        server.start();

        // Register server
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);
        NamingServer.RegisterRequest request = NamingServer.RegisterRequest.newBuilder()
                .setServiceName("DistLedger")
                .setServerHost("localhost")
                .setServerQualifier(serverQualifier)
                .setServerPort(Integer.toString(port))
                .build();

        try {
            stub.register(request);
            System.out.println("Server registered");

            System.out.println("Press Control C to shutdown the server:");

            // Hook de shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutdown hook executed");
                // Delete server
                NamingServer.DeleteRequest deleteChannel = NamingServer.DeleteRequest.newBuilder()
                        .setServiceName("DistLedger")
                        .setServerHost("localhost")
                        .setServerPort(Integer.toString(port))
                        .build();
                stub.delete(deleteChannel);
                System.out.println("Server deleted from naming server");
            }));


            server.awaitTermination();
        } catch (StatusRuntimeException e) {
            server.shutdownNow();
            System.out.println("Caught exception with description: " + e.getStatus().getDescription() + '\n');
            System.out.println("Server stopped");
        } finally {
            if (server != null)
                server.shutdown();
        }
    }
}
