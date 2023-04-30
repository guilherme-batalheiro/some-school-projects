package pt.tecnico.distledger.namingserver;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;
import pt.tecnico.distledger.namingserver.exception.NotPossibleToRemove;
import pt.tecnico.distledger.namingserver.exception.ServerAlreadyRegistered;
import pt.ulisboa.tecnico.distledger.contract.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc.*;

import static io.grpc.Status.FAILED_PRECONDITION;
import java.util.*;
import java.util.stream.Collectors;


public class NamingServerServiceImpl extends NamingServerServiceImplBase  {

    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    private final NamingServerState namingServerState;

    public NamingServerServiceImpl(NamingServerState namingServerState) {
        this.namingServerState = namingServerState;
    }

    public static void debug(String debugMessage) {
        if (DEBUG_FLAG) System.err.println("[\u001B[1m\u001B[96mDEBUG\u001B[0m] " + debugMessage);
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        NamingServerServiceImpl.debug("Request-----------------------------------------------------------------");
        NamingServerServiceImpl.debug("Received register request.");

        String serviceName = request.getServiceName();
        NamingServerServiceImpl.debug("Received service name: " + serviceName + ".");
        String serverHost = request.getServerHost();
        NamingServerServiceImpl.debug("Received server host: " + serverHost + ".");
        String serverPort = request.getServerPort();
        NamingServerServiceImpl.debug("Received server port: " + serverPort + ".");
        String serverQualifier = request.getServerQualifier();
        NamingServerServiceImpl.debug("Received server qualifier: " + serverQualifier + ".");

        try {
            this.namingServerState.registerServer(serviceName,
                    new ServerEntry(serverHost, serverPort, serverQualifier));

            RegisterResponse response = RegisterResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            NamingServerServiceImpl.debug("Successfully registered.");
        } catch (ServerAlreadyRegistered e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        NamingServerServiceImpl.debug("------------------------------------------------------------------------");
    }

    @Override
    public synchronized void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver) {
        NamingServerServiceImpl.debug("Request-----------------------------------------------------------------");
        NamingServerServiceImpl.debug("Received lookup request.");

        String serviceName = request.getServiceName();
        NamingServerServiceImpl.debug("Received service name: " + serviceName + ".");
        String serverQualifier = request.getServerQualifier();
        NamingServerServiceImpl.debug("Received server qualifier: " + serverQualifier + ".");

        List<ServerEntry> retrievedServers = namingServerState.lookupServer(serviceName, serverQualifier);

        LookupResponse response = LookupResponse.newBuilder()
                .addAllRetrievedServers(retrievedServers.stream().map(ServerEntry::proto).collect(Collectors.toList()))
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        NamingServerServiceImpl.debug("------------------------------------------------------------------------");
    }
    
    @Override
    public synchronized void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver){
        NamingServerServiceImpl.debug("Request-----------------------------------------------------------------");
        NamingServerServiceImpl.debug("Received delete request.");

        String serviceName = request.getServiceName();
        NamingServerServiceImpl.debug("Received service name: " + serviceName + ".");
        String serverHost = request.getServerHost();
        NamingServerServiceImpl.debug("Received server host: " + serverHost + ".");
        String serverPort = request.getServerPort();
        NamingServerServiceImpl.debug("Received server port: " + serverPort + ".");

        try {
            this.namingServerState.deleteServer(serviceName, serverHost, serverPort);

            DeleteResponse response = DeleteResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            NamingServerServiceImpl.debug("Successfully deleted.");
        } catch (NotPossibleToRemove e){
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        NamingServerServiceImpl.debug("------------------------------------------------------------------------");
    }
}
