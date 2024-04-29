package com.explorer.chat.servermanaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class ServerImpl implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);

    @Override
    public void run(String... args) throws Exception {

        log.info("Starting Chat Server...");
        
    }
}
