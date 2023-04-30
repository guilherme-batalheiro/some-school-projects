package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.exception.NotPossibleToRemove;
import pt.tecnico.distledger.namingserver.exception.ServerAlreadyRegistered;

import java.util.*;

public class ServiceEntry {
    private final String name;
    private final Set<ServerEntry> serverEntries;

    public ServiceEntry(String name) {
        this.name = name;
        this.serverEntries = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<ServerEntry> getServerEntries() {
        return serverEntries;
    }

    public void addServerEntry(ServerEntry serverEntry) throws ServerAlreadyRegistered {
        if (serverEntries.contains(serverEntry))
            throw new ServerAlreadyRegistered();
        else
            serverEntries.add(serverEntry);
    }

    public void removeServerEntry(String host, String port) throws NotPossibleToRemove {
        boolean removed = serverEntries.removeIf(serverEntry -> serverEntry.getServerHost().equals(host) && serverEntry.getServerPort().equals(port));
        if (!removed) {
            throw new NotPossibleToRemove();
        }
    }
}
