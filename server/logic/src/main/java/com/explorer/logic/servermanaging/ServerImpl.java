package com.explorer.logic.servermanaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServerImpl implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Logic Server...");

    }
}
