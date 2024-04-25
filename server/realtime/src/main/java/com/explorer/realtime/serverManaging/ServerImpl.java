package com.explorer.realtime.serverManaging;

import com.explorer.realtime.global.teamCode.TeamCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class ServerImpl implements CommandLineRunner {

    private final ServerInitializer serverInitializer;
    private final TeamCodeGenerator teamCodeGenerator;

    public ServerImpl(ServerInitializer serverInitializer, TeamCodeGenerator teamCodeGenerator) {
        this.serverInitializer = serverInitializer;
        this.teamCodeGenerator = teamCodeGenerator;
    }

    private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);
    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Server...");

        serverInitializer.initializeServer().subscribe(
                disposableServer -> {
                    log.info("Server started on port: {}", disposableServer.port());
                },
                error -> {
                    log.error("Failed to start Server: {}", error.getMessage());
                }
        );

    }
}
