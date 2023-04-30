package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import pt.tecnico.distledger.server.domain.operation.Operation;

import pt.tecnico.distledger.server.exception.*;

import pt.tecnico.distledger.server.exception.InsufficientFundsException;
import pt.tecnico.distledger.server.exception.NoSuchUserException;
import pt.tecnico.distledger.server.exception.UserAlreadyExistsException;


import java.util.*;

import static pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType.*;


public class ServerState {


    enum Mode {
        ACTIVE,
        INACTIVE
    }

    private List<Operation> ledger;

    private HashMap<String, User> users;

    private Mode mode;

    private VectorClock valueTS;

    private VectorClock replicaTS;

    private String qualifier;

    private Integer serverIndex;

    public ServerState(int seversNum, String qualifier) {
        this.valueTS = new VectorClock();
        this.replicaTS = new VectorClock();
        for (int i = 0; i < seversNum; i++) {
            this.valueTS.addTS(0);
            this.replicaTS.addTS(0);
        }


        this.users = new HashMap<>();
        this.ledger = new ArrayList<>();
        this.mode = Mode.ACTIVE;

        this.qualifier = qualifier;
        // Map from A, B, C, ... to 0, 1, 2, ...
        this.serverIndex = qualifier.charAt(0) - 'A';

        User broker = new User("broker", 1000);
        this.users.put(broker.getUserID(), broker);
    }

    public void incrementReplicaTS() {
        replicaTS.setTS(serverIndex, replicaTS.getTS(serverIndex) + 1);
    }

    public VectorClock getValueTS() {
        return valueTS;
    }

    public VectorClock getReplicaTS() {
        return replicaTS;
    }

    public Integer getValueTSLength() {
        return valueTS.getTSLength();
    }

    public Integer getReplicaTSLenght() {
        return replicaTS.getTSLength();
    }

    public boolean valueTSGE(VectorClock prev) {
        return valueTS.GE(prev);
    }

    public boolean replicaTSGE(VectorClock prev) {
        return replicaTS.GE(prev);
    }

    public String getQualifier() {
        return qualifier;
    }

    public synchronized List<Operation> getLedger() {
            return this.ledger;
    }

    public synchronized void validOpCreateAccount(String account) throws Exception {
        if (account.equals("broker"))
            throw new CannotCreateBrokerException();
        if (users.containsKey(account))
            throw new UserAlreadyExistsException();

    }

    public synchronized void createAccount(String account) throws Exception {
        validOpCreateAccount(account);

        User user = new User(account);
        users.put(account, user);
    }


    public synchronized int balance(String account) throws Exception {
        if (users.containsKey(account)) {
            return users.get(account).getBalance();
        } else {
            throw new NoSuchUserException();
        }
    }

    public synchronized void validOpTransferTo(String fromAccount, String destAccount) throws Exception {
        if(!users.containsKey(fromAccount) || !users.containsKey(destAccount)){
            throw new NoSuchUserException();
        }
    }

    public synchronized void transferTo(String fromAccount, String destAccount, int amount) throws Exception{
        validOpTransferTo(fromAccount, destAccount);

        User from = users.get(fromAccount);
        User dest = users.get(destAccount);

        if (from == null || from.getBalance() < amount || dest == null) {
            throw new InsufficientFundsException();
        }

        from.setBalance(from.getBalance()-amount);
        dest.setBalance(dest.getBalance()+amount);
    }

    public synchronized boolean isActive() {
        return mode.equals(Mode.ACTIVE);
    }

    public synchronized void changeModeToInactive() throws Exception {
        if (mode.equals(Mode.INACTIVE))
            throw new ServerAlreadyInactiveException();
        mode = Mode.INACTIVE;
    }

    public synchronized void changeModeToActive() throws Exception {
        if (mode.equals(Mode.ACTIVE))
            throw new ServerAlreadyActiveException();
        mode = Mode.ACTIVE;
    }

    public VectorClock createTsFromPrev(VectorClock prev) {
        VectorClock ts = new VectorClock();

        for (int i = 0; i < prev.getTSLength(); i++) {
            if (i == serverIndex)
                ts.addTS(replicaTS.getTS(serverIndex));
            else
                ts.addTS(prev.getTS(i));
        }

        return ts;
    }

    public synchronized void implementOp(Operation operation) throws Exception {
        switch (operation.getClass().getSimpleName()) {
            case "CreateOp":
                createAccount(operation.getAccount());
                break;
            case "TransferOp":
                transferTo(operation.getAccount(),
                        ((TransferOp) operation).getDestAccount(),
                        ((TransferOp) operation).getAmount());
                break;
            default:
                throw new UnspecifiedOperationTypeException();
        }
    }

    public boolean inLedger(Operation op) {
        for (Operation operation : ledger) {
            if (op.getTs().equals(operation.getTs())) {
                return true;
            }
        }
        return false;
    }
    public synchronized void saveOp(Operation op) {
        for (int i = 0; i < ledger.size(); i++) {
            if (!op.getTs().GE(ledger.get(i).getTs())) {
                ledger.add(i, op);
                return;
            }
        }

        ledger.add(op);
    }

    public DistLedgerCommonDefinitions.LedgerState getLedgerGrpcFormat() {

        DistLedgerCommonDefinitions.LedgerState.Builder ledgerState =
                DistLedgerCommonDefinitions.LedgerState.newBuilder();

        for (Operation op : ledger) {
            DistLedgerCommonDefinitions.Operation.Builder opContent = DistLedgerCommonDefinitions.Operation.newBuilder();
            opContent.setUserId(op.getAccount());
            opContent.setPrevTS(op.getPrev().proto());
            opContent.setTS(op.getTs().proto());

            switch (op.getClass().getSimpleName()) {
                case "CreateOp":
                    opContent.setType(OP_CREATE_ACCOUNT);
                    break;
                case "TransferOp":
                    opContent.setType(OP_TRANSFER_TO);
                    opContent.setDestUserId(((TransferOp) op).getDestAccount());
                    opContent.setAmount(((TransferOp) op).getAmount());
                    break;
            }

            ledgerState.addLedger(opContent.build());
        }

        return ledgerState.build();
    }

    public static List<Operation> convertLedgerGrpcToOperationList(
            DistLedgerCommonDefinitions.LedgerState proto
    ) {
        List<Operation> ledger = new ArrayList<>();

        for (DistLedgerCommonDefinitions.Operation op : proto.getLedgerList()) {
            switch (op.getType()) {
                case OP_TRANSFER_TO:
                    ledger.add(new TransferOp(
                        op.getUserId(),
                        op.getDestUserId(),
                        op.getAmount(),
                        VectorClock.convertVectorClockGrpcToVectorClock(op.getTS()),
                        VectorClock.convertVectorClockGrpcToVectorClock(op.getPrevTS()),
                        op.getStable()));
                    break;
                case OP_CREATE_ACCOUNT:
                    ledger.add(new CreateOp(
                        op.getUserId(),
                        VectorClock.convertVectorClockGrpcToVectorClock(op.getTS()),
                        VectorClock.convertVectorClockGrpcToVectorClock(op.getPrevTS()),
                        op.getStable()));
                    break;
            }
        }

        return ledger;
    }
}
