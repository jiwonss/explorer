package com.explorer.move.global.component.teamcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TeamCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(TeamCodeGenerator.class);
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CACHE_SIZE = 100;
    private static final int CODE_LENGTH = 8;
    private final Queue<String> codeCache = new ConcurrentLinkedQueue<>();

    public TeamCodeGenerator() {
        preloadCodesAsync();
    }

    public void preloadCodesAsync() {
        log.info("start Generating teamcode...");

        Mono.fromRunnable(this::preloadCodes)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void preloadCodes() {
        for (int i = 0; i < CACHE_SIZE; i++) {
            codeCache.offer(generateRandomCode(CODE_LENGTH));
        }
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    public Mono<String> getCode() {
        String code = codeCache.poll();
        if (codeCache.size() < CACHE_SIZE / 2) {
            preloadCodesAsync();
        }
        return Mono.justOrEmpty(code);
    }
}
