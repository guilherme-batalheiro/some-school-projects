package pt.tecnico.distledger.server;


import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.VectorClock;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import java.util.List;

import static io.grpc.Status.UNAVAILABLE;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);
    private final ServerState serverState;
    public CrossServerServiceImpl(ServerState serverState) {
        this.serverState = serverState;
    }

    public static void debug(String debugMessage) {
        if (DEBUG_FLAG) System.err.println("[\u001B[1m\u001B[96mDEBUG\u001B[0m] " + debugMessage);
    }

    public static void exception(String exceptionMessage) {
        System.err.println("[\u001B[1m\u001B[31mEXCEPTION\u001B[0m] " + exceptionMessage);
    }

    @Override
    public synchronized void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
        CrossServerServiceImpl.debug("Request-----------------------------------------------------------------");
        CrossServerServiceImpl.debug("Received a propagate state request.");

        if (!serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        try {
            List<Operation> incomeLedger = ServerState.convertLedgerGrpcToOperationList(request.getState());
            /* Adding received operations to server's Ledger State */
            for (Operation op : incomeLedger) {
                CrossServerServiceImpl.debug("Received operation: "
                        + op.getClass().getSimpleName() + " "
                        + op.getAccount() + " | with ts: "
                        + op.getTs() + " | with prev: "
                        + op.getPrev());

                CrossServerServiceImpl.debug("Compare if " + serverState.getReplicaTS()
                        + " is not greater or equal than "
                        + op.getTs());
                /* Check if received operations aren't in the Ledger State already */
                if (!(serverState.getReplicaTS().GE(op.getTs())) && !serverState.inLedger(op)) {
                    serverState.saveOp(op);
                    CrossServerServiceImpl.debug("Operation saved to ledger.");

                    CrossServerServiceImpl.debug("Compare if " + serverState.getValueTS()
                            + " is greater or equal than " + op.getPrev());
                    /* Check if operation is stable, if so implement it, otherwise set operation as unstable */
                    if (serverState.getValueTS().GE(op.getPrev())) {
                        op.setStable(true);
                        try {
                            serverState.implementOp(op);
                        } catch (Exception e) {
                            CrossServerServiceImpl.exception(e.getMessage());
                        }

                        CrossServerServiceImpl.debug("Implement operation.");
                        CrossServerServiceImpl.debug("Merge value ts: " + serverState.getValueTS()
                                + "  | with op ts: " + op.getTs() + ".");
                        serverState.getValueTS().merge(op.getTs());
                        CrossServerServiceImpl.debug("Merge result: " + serverState.getValueTS());
                    } else
                        op.setStable(false);
                }
            }

            CrossServerServiceImpl.debug("Merge: " + serverState.getReplicaTS()
                    + " with " + VectorClock.convertVectorClockGrpcToVectorClock(request.getReplicaTS()) + ".");
            serverState.getReplicaTS().merge(VectorClock.convertVectorClockGrpcToVectorClock(request.getReplicaTS()));
            CrossServerServiceImpl.debug("Merge result: " + serverState.getReplicaTS());
            /* Implement new stable operations */
            for (Operation op : serverState.getLedger()) {
                CrossServerServiceImpl.debug("Checking if operation is not stable and can be implemented: "
                        + op.getClass().getSimpleName() + " "
                        + op.getAccount() + " | with ts: "
                        + op.getTs() + " | with prev: "
                        + op.getPrev() + " | stable: " + op.getStable());
                if (!op.getStable() && serverState.getValueTS().GE(op.getPrev())) {
                    op.setStable(true);
                    try {
                        serverState.implementOp(op);
                    } catch (Exception e) {
                        CrossServerServiceImpl.exception(e.getMessage());
                    }
                    CrossServerServiceImpl.debug("Operation implemented.");
                    CrossServerServiceImpl.debug("Merge value ts: " + serverState.getValueTS()
                            + " | with op ts: " + op.getTs() + ".");
                    serverState.getValueTS().merge(op.getTs());
                    CrossServerServiceImpl.debug("Merge result: " + serverState.getValueTS());
                }
            }

            PropagateStateResponse response = PropagateStateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            CrossServerServiceImpl.exception(e.getMessage());
        }
        CrossServerServiceImpl.debug("------------------------------------------------------------------------");
    }
}
