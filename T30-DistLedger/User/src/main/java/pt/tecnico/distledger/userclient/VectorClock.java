package pt.tecnico.distledger.userclient;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.ArrayList;

public class VectorClock {

    private final ArrayList<Integer> timeStamps;

    public VectorClock() {
        timeStamps = new ArrayList<>();
    }

    public VectorClock(Integer numServers) {
        timeStamps = new ArrayList<>();

        for (int i = 0; i < numServers; i++) {
            timeStamps.add(0);
        }
    }

    public Integer getTS(Integer i) {
        return timeStamps.get(i);
    }

    public void addTS(Integer value) {
        timeStamps.add(value);
    }

    public void setTS(Integer i, Integer value) {
        timeStamps.set(i, value);
    }

    public int getTSLength() {
        return timeStamps.size();
    }

    public void merge(VectorClock v_2) {
        for (int i = 0; i < getTSLength(); i++) {
            if (getTS(i) < v_2.getTS(i))
                setTS(i, v_2.getTS(i));
        }
    }

    public static VectorClock convertToVectorClock(DistLedgerCommonDefinitions.VectorClock vc) {
        VectorClock vectorClock = new VectorClock();
        for (int i = 0; i < vc.getTsCount(); i++) {
            vectorClock.addTS(vc.getTs(i));
        }
        return vectorClock;
    }

    public static DistLedgerCommonDefinitions.VectorClock convertToUserDistLedgerVC(VectorClock vc) {
        DistLedgerCommonDefinitions.VectorClock.Builder vectorClock = DistLedgerCommonDefinitions.VectorClock.newBuilder();

        for (int i = 0; i < vc.getTSLength(); i++) {
            vectorClock.addTs(vc.getTS(i));
        }
        return vectorClock.build();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append('[');
        for (Integer timeStamp : timeStamps) {
            string.append(timeStamp).append(", ");
        }
        string.deleteCharAt(string.length() - 1)
                .deleteCharAt(string.length() - 1)
                .append(']');

        return string.toString();
    }
}
