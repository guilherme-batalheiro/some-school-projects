package pt.tecnico.distledger.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.VectorClock;
import pt.tecnico.distledger.server.exception.ServerAlreadyActiveException;
import pt.tecnico.distledger.server.exception.ServerAlreadyInactiveException;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import java.util.List;

import static io.grpc.Status.*;

public class AdminServiceImpl extends AdminServiceImplBase {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);
    private final ServerState serverState;
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    public static void debug(String debugMessage) {
        if (DEBUG_FLAG) System.err.println("[\u001B[1m\u001B[96mDEBUG\u001B[0m] " + debugMessage);
    }

    public static void exception(String exceptionMessage) {
        System.err.println("[\u001B[1m\u001B[31mEXCEPTION\u001B[0m] " + exceptionMessage);
    }

    public AdminServiceImpl(ServerState serverState) {
        this.serverState = serverState;
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        AdminServiceImpl.debug("Request-----------------------------------------------------------------");
        AdminServiceImpl.debug("Received a active request.");
        try {
            serverState.changeModeToActive();

            ActivateResponse response = ActivateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerAlreadyActiveException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
            AdminServiceImpl.exception(e.getMessage());
        } catch (Exception e) {
            AdminServiceImpl.exception(e.getMessage());
        }
        AdminServiceImpl.debug("------------------------------------------------------------------------");
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        AdminServiceImpl.debug("Request-----------------------------------------------------------------");
        AdminServiceImpl.debug("Received a deactivate request.");
        try {
            serverState.changeModeToInactive();

            DeactivateResponse response = DeactivateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerAlreadyInactiveException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
            AdminServiceImpl.exception(e.getMessage());
        } catch (Exception e) {
            AdminServiceImpl.exception(e.getMessage());
        }
        AdminServiceImpl.debug("------------------------------------------------------------------------");
    }

    @Override
    public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        AdminServiceImpl.debug("Request-----------------------------------------------------------------");
        AdminServiceImpl.debug("Received a get ledger request.");
        try {
            getLedgerStateResponse response = getLedgerStateResponse.newBuilder()
                    .setLedgerState(serverState.getLedgerGrpcFormat())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            AdminServiceImpl.exception(e.getMessage());
        }
        AdminServiceImpl.debug("------------------------------------------------------------------------");
    }

    public List<ServerEntry> searchForServers() throws StatusRuntimeException {
        return stub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").build()).getRetrievedServersList();
    }

    @Override
    public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
        AdminServiceImpl.debug("Request-----------------------------------------------------------------");
        AdminServiceImpl.debug("Received a gossip request.");
        List<ServerEntry> seversEntries =  searchForServers();

        try {
            for (ServerEntry server : seversEntries) {
                if (server.getQualifier().equals(serverState.getQualifier()))
                    continue;

                ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                        server.getHost() + ":" + server.getPort()).usePlaintext().build();

                DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub tmpStub =
                        DistLedgerCrossServerServiceGrpc.newBlockingStub(tmpChannel);

                tmpStub.propagateState(PropagateStateRequest.newBuilder()
                        .setState(serverState.getLedgerGrpcFormat())
                        .setReplicaTS(serverState.getReplicaTS().proto())
                        .build());

                tmpChannel.shutdownNow();

                for (DistLedgerCommonDefinitions.Operation op : serverState.getLedgerGrpcFormat().getLedgerList()) {
                    AdminServiceImpl.debug("Sent op: " + op.getType() + " with ts: "
                            + VectorClock.convertVectorClockGrpcToVectorClock(op.getTS())
                            + " with prev: "
                            + VectorClock.convertVectorClockGrpcToVectorClock(op.getPrevTS())
                            + "."
                            );
                }

                AdminServiceImpl.debug("Propagated state to server: " + server.getQualifier() + ".");
            }

            GossipResponse response = GossipResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            AdminServiceImpl.exception(e.getMessage());
        }
        AdminServiceImpl.debug("------------------------------------------------------------------------");
    }
}
