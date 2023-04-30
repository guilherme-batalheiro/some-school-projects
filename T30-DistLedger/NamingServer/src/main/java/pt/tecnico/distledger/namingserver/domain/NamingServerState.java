package pt.tecnico.distledger.namingserver.domain;

import java.util.*;

public class NamingServerState {

    private final HashMap<String, ServiceEntry> serviceEntries;

    public NamingServerState() {
        this.serviceEntries = new HashMap<>();
    }

    public synchronized void registerServer(String serviceName, ServerEntry serverEntry) throws Exception {
        ServiceEntry serviceEntry;
        if (!serviceEntries.containsKey(serviceName)) {
            serviceEntry = new ServiceEntry(serviceName);
            this.serviceEntries.put(serviceEntry.getName(), serviceEntry);
        } else {
            serviceEntry = serviceEntries.get(serviceName);
        }

        serviceEntry.addServerEntry(serverEntry);
    }

    public synchronized List<ServerEntry> lookupServer(String serviceName, String qualifier) {
        List<ServerEntry> retrievedServers;

        if (!(serviceEntries.containsKey(serviceName))) {
            return new ArrayList<>();
        }

        if (qualifier.isEmpty()) {
            return new ArrayList<>(serviceEntries.get(serviceName).getServerEntries());
        }

        retrievedServers = new ArrayList<>();

        for (ServerEntry serverEntry : serviceEntries.get(serviceName).getServerEntries()) {
            if (serverEntry.getServerQualifier().equals(qualifier)) {
                retrievedServers.add(serverEntry);
            }
        }
        return retrievedServers;
    }
    
    public synchronized void deleteServer(String serviceName, String host, String port) throws Exception {
        if (serviceEntries.containsKey(serviceName)) {
            serviceEntries.get(serviceName).removeServerEntry(host,port);
        }
    }

}
