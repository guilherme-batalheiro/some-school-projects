package pt.tecnico.distledger.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.VectorClock;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.exception.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import static io.grpc.Status.*;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);
    private final ServerState serverState;

    public static void debug(String debugMessage) {
        if (DEBUG_FLAG) System.err.println("[\u001B[1m\u001B[96mDEBUG\u001B[0m] " + debugMessage);
    }

    public static void exception(String exceptionMessage) {
        System.err.println("[\u001B[1m\u001B[31mEXCEPTION\u001B[0m] " + exceptionMessage);
    }

    public UserServiceImpl(ServerState serverState) {
        this.serverState = serverState;
    }


    @Override
    public synchronized void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        UserServiceImpl.debug("Request-----------------------------------------------------------------");
        UserServiceImpl.debug("ValueTs: " + serverState.getValueTS()
                + "replicaTs" + serverState.getReplicaTS()  + ".");
        UserServiceImpl.debug("Received a create account request.");
        if (!serverState.isActive()) {
            UserServiceImpl.debug("Server is deactivated.");
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        // Get args from request.
        String account = request.getUserId();
        UserServiceImpl.debug("Received user id: " + account + ".");
        VectorClock prev = VectorClock.convertVectorClockGrpcToVectorClock(request.getPrev());
        UserServiceImpl.debug("Received prev vector clock: " + prev + ".");

        // Check with the vectors clocks have the same size.
        if(serverState.getValueTSLength() != prev.getTSLength()) {
            UserServiceImpl.debug("Vector clock with different size.");
            responseObserver.onError(INTERNAL
                    .withDescription("Server vector clock have a different size than vector clock provided ")
                    .asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        // Increment the vector clock in the server state by one to indicate that the server received an operation.
        serverState.incrementReplicaTS();

        // Create ts from prev by replacing prev’s ith element with the ith element of its replica timestamp.
        VectorClock ts = serverState.createTsFromPrev(prev);

        // Save the operation in the server state.
        Operation op = new CreateOp(account, ts, prev, serverState.valueTSGE(prev));
        serverState.saveOp(op);

        // Send response to the client with the new vector clock.
        CreateAccountResponse response = CreateAccountResponse.newBuilder()
                .setNew(ts.proto())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        UserServiceImpl.debug("Sent to client new vector clock: " + ts + ".");


        // Realize the operation if the operation is stable.
        if (op.stable()) {
            UserServiceImpl.debug("Operation is stable.");
            try {
                serverState.createAccount(account);
                UserServiceImpl.debug("Server created the account.");
            } catch(Exception e){
                UserServiceImpl.exception(e.getMessage());
            }
            serverState.getValueTS().merge(ts);
        } else {
            UserServiceImpl.debug("Operation is not stable.");
        }
        UserServiceImpl.debug("ValueTs: " + serverState.getValueTS()
                + " replicaTs" + serverState.getReplicaTS()  + ".");
        UserServiceImpl.debug("------------------------------------------------------------------------");
    }

    @Override
    public synchronized void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        UserServiceImpl.debug("Request-----------------------------------------------------------------");
        UserServiceImpl.debug("ValueTs: " + serverState.getValueTS()
                + "replicaTs" + serverState.getReplicaTS()  + ".");
        UserServiceImpl.debug("Received a transfer to request.");
        if (!serverState.isActive()) {
            UserServiceImpl.debug("Server is deactivated.");
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        // Get args from request.
        String fromAccount = request.getAccountFrom();
        UserServiceImpl.debug("Received from account: " + fromAccount + ".");
        String destAccount = request.getAccountTo();
        UserServiceImpl.debug("Received dest account: " + destAccount + ".");
        int amount = request.getAmount();
        UserServiceImpl.debug("Received amount: " + amount + ".");
        VectorClock prev = VectorClock.convertVectorClockGrpcToVectorClock(request.getPrev());
        UserServiceImpl.debug("Received prev vector clock: " + prev + ".");

        // Check with the vectors clocks have the same size.
        if(serverState.getValueTSLength() != prev.getTSLength()) {
            responseObserver.onError(INTERNAL
                    .withDescription("Server vector clock have a different size than vector clock provided ")
                    .asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        // increment the vector clock in the server state by one to indicate that the server received an operation.
        serverState.incrementReplicaTS();

        // Create ts from prev by replacing prev’s ith element with the ith element of its replica timestamp.
        VectorClock ts = serverState.createTsFromPrev(prev);

        // Save the operation in the server state.
        Operation op = new TransferOp(fromAccount, destAccount, amount, ts, prev,
                serverState.valueTSGE(prev));
        serverState.saveOp(op);

        // Send response to the client with the new vector clock.
        TransferToResponse response = TransferToResponse.newBuilder()
                .setNew(ts.proto())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        UserServiceImpl.debug("Sent to client new vector clock: " + ts + ".");


        // Realize the operation if the operation is stable.
        if (op.stable()) {
            UserServiceImpl.debug("Operation is stable.");
            try {
                serverState.transferTo(fromAccount, destAccount, amount);
                UserServiceImpl.debug("Server made the transference.");
            } catch(Exception e){
                UserServiceImpl.exception(e.getMessage());
            }
            serverState.getValueTS().merge(ts);
        } else {
            UserServiceImpl.debug("Operation is not stable.");
        }
        UserServiceImpl.debug("ValueTs: " + serverState.getValueTS()
                + " replicaTs" + serverState.getReplicaTS()  + ".");
        UserServiceImpl.debug("------------------------------------------------------------------------");
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        UserServiceImpl.debug("Request-----------------------------------------------------------------");
        UserServiceImpl.debug("ValueTs: " + serverState.getValueTS()
                + " replicaTs" + serverState.getReplicaTS()  + ".");
        UserServiceImpl.debug("Received a balance request.");
        if (!serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        // Get args from request.
        String account = request.getUserId();
        UserServiceImpl.debug("Received user id: " + account + ".");
        VectorClock prev = VectorClock.convertVectorClockGrpcToVectorClock(request.getPrev());
        UserServiceImpl.debug("Received prev vector clock: " + prev + ".");

        // Check with the vectors clocks have the same size.
        if(serverState.getValueTSLength() != prev.getTSLength()) {
            responseObserver.onError(INTERNAL
                    .withDescription("Server vector clock have a different size than vector clock provided ")
                    .asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        UserServiceImpl.debug("Compare prev with value: " + serverState.getValueTS() + ".");
        // Check if the is not in the future
        if (!serverState.valueTSGE(prev)) {
            responseObserver.onError(INTERNAL
                    .withDescription("Server vector clock not GE than vector clock provided ").asRuntimeException());
            UserServiceImpl.debug("------------------------------------------------------------------------");
            return;
        }

        // Send balance
        try {
            int result = serverState.balance(account);
            BalanceResponse response = BalanceResponse.newBuilder()
                    .setValue(result)
                    .setNew(serverState.getValueTS().proto())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            UserServiceImpl.debug("Sent to client new vector clock: " + serverState.getReplicaTS() + ".");
            UserServiceImpl.debug("Sent to client value: " + result + ".");
        } catch (NoSuchUserException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        UserServiceImpl.debug("------------------------------------------------------------------------");
    }
}

