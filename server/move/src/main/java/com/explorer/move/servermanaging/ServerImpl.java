package com.explorer.move.servermanaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service
public class ServerImpl implements CommandLineRunner {

    private final ServerInitializer serverInitializer;

    public ServerImpl(ServerInitializer serverInitializer) {
        this.serverInitializer = serverInitializer;
    }

    private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);
    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Server...");

        serverInitializer.initializeServer().subscribe(
                connection -> {
                    InetSocketAddress localAddress = (InetSocketAddress) connection.channel().localAddress();
                    int port = localAddress.getPort();
                    log.info("Server started on port: {}", port);
                },
                error -> {
                    log.error("Failed to start Server: {}", error.getMessage());
                }
        );

    }
}
