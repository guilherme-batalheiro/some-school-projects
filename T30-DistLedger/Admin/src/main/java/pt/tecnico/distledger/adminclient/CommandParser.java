package pt.tecnico.distledger.adminclient;

import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.adminclient.exception.ServerEntryNotFound;
import pt.tecnico.distledger.adminclient.grpc.AdminService;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;

import java.util.*;

import static pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final AdminService adminService;

    public CommandParser() {
        this.adminService = new AdminService();
    }
    
    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            switch (cmd) {
                case ACTIVATE:
                    this.activate(line);
                    break;

                case DEACTIVATE:
                    this.deactivate(line);
                    break;

                case GET_LEDGER_STATE:
                    this.dump(line);
                    break;

                case GOSSIP:
                    this.gossip(line);
                    break;

                case HELP:
                    this.printUsage();
                    break;

                case EXIT:
                    exit = true;
                    break;

                default:
                    break;
            }
        }

        adminService.shutDownService();
    }

    private void activate(String line){
        AdminClientMain.debug("Call activate");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }

        String server = split[1];
        AdminClientMain.debug("Server: "+ server);
        
        try {
            adminService.activate(server);
            System.out.println("OK\n");
        } catch (ServerEntryNotFound e) {
            System.out.println("Caught exception with description: " + e.getMessage() + '\n');
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        }
    }

    private void deactivate(String line){
        AdminClientMain.debug("Call deactivate");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];

        try {
            adminService.deactivate(server);
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        } catch (ServerEntryNotFound e) {
            System.out.println("Caught exception with description: " + e.getMessage() + '\n');
        }
    }

    public static String converteVectorClockGRPCToString(DistLedgerCommonDefinitions.VectorClock vc) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        for (Integer ts : vc.getTsList())
            s.append(ts.toString()).append(", ");

        s.deleteCharAt(s.length() - 1)
                .deleteCharAt(s.length() - 1)
                .append(']');
        return s.toString();
    }

    private void dump(String line){
        AdminClientMain.debug("Call dump");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }

        String server = split[1];

        try {
            LedgerState ledgerState = adminService.getLedgerState(server);
            System.out.println("OK");
            System.out.println("ledgerState {");
            for (Operation op : ledgerState.getLedgerList()) {
                String leadgerString =
                        "  ledger {\n" +
                                "    type: \"" + op.getType() + "\"\n" +
                                "    ts: \"" + converteVectorClockGRPCToString(op.getTS()) + "\"\n" +
                                "    prev: \"" + converteVectorClockGRPCToString(op.getPrevTS()) + "\"\n" +
                                "    userId: \"" + op.getUserId() + "\"\n";
                if(op.getType() == OP_TRANSFER_TO)
                    leadgerString +=
                            "    destUserId: \"" + op.getDestUserId() + "\"\n" +
                                    "    amount: \"" + op.getAmount() + "\"\n";
                leadgerString += "  }\n";
                System.out.print(leadgerString);
            }
            System.out.println("}\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        } catch (ServerEntryNotFound e) {
            System.out.println("Caught exception with description: " + e.getMessage() + '\n');
        }
    }

    private void gossip(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }

        String server = split[1];

        try {
            adminService.gossip(server);
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        } catch (ServerEntryNotFound e) {
            System.out.println("Caught exception with description: " + e.getMessage() + '\n');
        }

    }
    private void printUsage() {
        System.out.println("Usage:\n" +
                "- activate <server>\n" +
                "- deactivate <server>\n" +
                "- getLedgerState <server>\n" +
                "- gossip <server>\n" +
                "- exit\n");
    }
}
