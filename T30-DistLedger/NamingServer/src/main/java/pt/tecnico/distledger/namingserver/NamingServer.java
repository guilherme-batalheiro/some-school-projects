package pt.tecnico.distledger.namingserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.namingserver.domain.NamingServerState;

import java.io.IOException;

public class NamingServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(NamingServer.class.getSimpleName());

        final int port = 5001;
        final BindableService namingServerImpl = new NamingServerServiceImpl(new NamingServerState());

        // Create a new server to listen on port
        Server server = ServerBuilder.forPort(port).addService(namingServerImpl).build();

        // Start the servers
        server.start();

        // Server threads are running in the background.
        System.out.println("Server started");

        // Do not exit the main thread. Wait until server is terminated.
        server.awaitTermination();
    }
}
