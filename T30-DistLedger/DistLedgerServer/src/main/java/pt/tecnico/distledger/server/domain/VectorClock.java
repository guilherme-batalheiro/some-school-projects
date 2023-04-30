package pt.tecnico.distledger.server.domain;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.ArrayList;
import java.util.Objects;

public class VectorClock {

    private final ArrayList<Integer> timeStamps;

    public VectorClock() {
       timeStamps = new ArrayList<>();
    }

    public static VectorClock convertVectorClockGrpcToVectorClock(DistLedgerCommonDefinitions.VectorClock vc) {
        VectorClock vectorClock = new VectorClock();
        for (int i = 0; i < vc.getTsCount(); i++)
            vectorClock.addTS(vc.getTs(i));

        return vectorClock;
    }

    public Integer getTS(Integer i) {
       return timeStamps.get(i);
    }

    public void setTS(Integer i, Integer value) {
        timeStamps.set(i, value);
    }

    public void addTS(Integer value) {
        timeStamps.add(value);
    }

    public int getTSLength() {
        return timeStamps.size();
    }

    public boolean GE(VectorClock v) {
        for (int i = 0; i < timeStamps.size(); i++) {
            if (timeStamps.get(i) < v.getTS(i))
                return false;
        }
        return true;
    }

    public DistLedgerCommonDefinitions.VectorClock proto() {
        DistLedgerCommonDefinitions.VectorClock.Builder vBuilder = DistLedgerCommonDefinitions.VectorClock.newBuilder();
        for (Integer timeStamp : timeStamps) {
            vBuilder.addTs(timeStamp);
        }

        return vBuilder.build();
    }

    public void merge(VectorClock v_2) {
        for (int i = 0; i < getTSLength(); i++) {
            if (getTS(i) < v_2.getTS(i))
                setTS(i, v_2.getTS(i));
        }
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorClock)) {
            return false;
        }
        VectorClock other = (VectorClock) obj;

        if (this.getTSLength() != other.getTSLength())
            return false;

        for (int i = 0; i < this.getTSLength(); i++) {
            if (!Objects.equals(this.getTS(i), other.getTS(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamps);
    }
}
