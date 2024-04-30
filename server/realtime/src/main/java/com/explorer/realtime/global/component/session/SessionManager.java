package com.explorer.realtime.global.component.session;

import org.springframework.stereotype.Component;
import reactor.netty.Connection;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<String, Connection> uidToConnectionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Connection, String> connectionToUidMap = new ConcurrentHashMap<>();

    public void setConnection(String uid, Connection connection) {
        uidToConnectionMap.put(uid, connection);
        connectionToUidMap.put(connection, uid);
    }

    public Connection getConnection(String uid) {
        return uidToConnectionMap.get(uid);
    }

    public String getUid(Connection connection) {
        return connectionToUidMap.get(connection);
    }

    public void removeConnection(String uid) {
        Connection connection = getConnection(uid);
        if (connection != null) {
            uidToConnectionMap.remove(uid);
            connectionToUidMap.remove(connection);
        }
    }
}
