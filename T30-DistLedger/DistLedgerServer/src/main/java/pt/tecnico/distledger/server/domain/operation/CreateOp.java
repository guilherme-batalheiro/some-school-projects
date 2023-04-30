package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.VectorClock;

public class CreateOp extends Operation {

    public CreateOp(String account, VectorClock ts, VectorClock prev, Boolean stable) {
        super(account, ts, prev, stable);
    }

}
