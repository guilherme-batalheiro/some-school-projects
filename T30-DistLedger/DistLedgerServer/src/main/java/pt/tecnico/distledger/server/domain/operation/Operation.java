package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.VectorClock;

public class Operation {
    private String account;

    private final VectorClock prev;

    private final VectorClock ts;

    private Boolean stable;

    public Operation(String fromAccount, VectorClock ts, VectorClock prev, Boolean stable) {
        this.account = fromAccount;
        this.ts = ts;
        this.prev = prev;
        this.stable = stable;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public VectorClock getTs() {
        return ts;
    }

    public VectorClock getPrev() {
        return prev;
    }

    public Boolean getStable() {
        return stable;
    }

    public void setStable(Boolean stable) {
        this.stable = stable;
    }

    public Boolean stable() {
        return stable;
    }
}
