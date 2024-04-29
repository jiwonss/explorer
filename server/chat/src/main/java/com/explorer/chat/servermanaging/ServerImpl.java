package com.explorer.chat.servermanaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class ServerImpl implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);

    private final ServerInitializer serverInitializer;

    public ServerImpl(ServerInitializer serverInitializer) {
        this.serverInitializer = serverInitializer;
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Chat Server...");

        serverInitializer.initializeServer().subscribe(
                disposableServer -> log.info("Chat Server started on port: {}", disposableServer.port()),
                error -> log.error("Failed to start Chat Server: {}", error.getMessage())
        );

    }
}
