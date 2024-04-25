package com.explorer.realtime.serverManaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

@Component
public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Mono<Void> handleRequest(NettyInbound inbound, NettyOutbound outbound) {

        return inbound
                .receive()                                      // 1) receive data
                .asString()                                     // 2) convert data : byte -> string
                .flatMap(msg -> {                               // 3) process data
                    try{

                        JSONObject json = new JSONObject(msg);      // parse string to json
                        log.info("Received Json Data: {}", json);   // logging

                        String event = json.getString("event");

                        switch(event) {
                            case "createWaitingRoom" :
                                log.info("create waiting room");
                                break;

                            case "joinWaitingRoom":
                                log.info("join waiting room");
                                break;

                            case "leaveWaitingRoom":
                                log.info("leave waiting room");
                                break;

                            case "startGame" :
                                log.info("start game");
                                break;

                            case "restartGame":
                                log.info("restart game");
                                break;

                            case "endGame":
                                log.info("endGame");
                                break;
                        }

                        return outbound.sendString(Mono.just("success"));   // echoing

                    } catch (JSONException e) {
                        log.error("ERROR : {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .then();                                        // 4) complete reactive sequence

    }
}
