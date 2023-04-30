package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.VectorClock;

public class TransferOp extends Operation {
    private String destAccount;
    private int amount;

    public TransferOp(String fromAccount, String destAccount, int amount, VectorClock ts, VectorClock prev,
                      Boolean stable) {
        super(fromAccount, ts, prev, stable);
        this.destAccount = destAccount;
        this.amount = amount;
    }

    public String getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(String destAccount) {
        this.destAccount = destAccount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
