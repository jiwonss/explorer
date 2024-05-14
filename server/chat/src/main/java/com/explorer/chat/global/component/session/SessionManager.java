package com.explorer.chat.global.component.session;

import org.springframework.stereotype.Component;
import reactor.netty.Connection;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<Long, Connection> uidToConnectionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Connection, Long> connectionToUidMap = new ConcurrentHashMap<>();

    public void setConnection(Long userId, Connection connection) {
        uidToConnectionMap.put(userId, connection);
        connectionToUidMap.put(connection, userId);
    }

    public Connection getConnection(Long userId) {
        return uidToConnectionMap.get(userId);
    }

    public Long getUid(Connection connection) {
        return connectionToUidMap.get(connection);
    }

    public void removeConnection(Long userId) {
        Connection connection = getConnection(userId);
        if (connection != null) {
            uidToConnectionMap.remove(userId);
            connectionToUidMap.remove(connection);
        }
    }
}
