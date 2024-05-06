package com.explorer.logic.servermanaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements CommandLineRunner {

    private final ServerInitializer serverInitializer;

    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Logic Server...");
        serverInitializer.initializeServer().subscribe(
                disposableServer -> log.info("Logic server started on port: {}", disposableServer.port()),
                error -> log.error("Failed to start Logic server: {}", error.getMessage())
        );

    }
}
