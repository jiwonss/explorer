package com.explorer.realtime.servermanaging;

import com.explorer.realtime.gamedatahandling.GameDataHandler;
import com.explorer.realtime.sessionhandling.ingame.InGameSessionHandler;
//import com.explorer.realtime.sessionhandling.ingame.StartGamebackup;
import com.explorer.realtime.sessionhandling.waitingroom.WaitingRoomSessionHandler;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

@Component
@RequiredArgsConstructor
public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final WaitingRoomSessionHandler waitingRoomSessionHandler;
    private final InGameSessionHandler inGameSessionHandler;
    private final GameDataHandler gameDataHandler;

    public Mono<Void> handleRequest(NettyInbound inbound, NettyOutbound outbound) {

        return inbound
                .receive()                                      // 1) receive data
                .asString()                                     // 2) convert data : byte -> string
                .flatMap(msg -> {                               // 3) process data
                    try{
                        JSONObject json = new JSONObject(msg);      // parse string to json
                        log.info("Received Json Data: {}", json);

                        inbound.withConnection(connection -> {
                            String type = json.getString("type");

                            switch(type) {
                                case "waitingRoomSession" :
                                    log.info("waiting room");
                                    waitingRoomSessionHandler.waitingRoomHandler(json, connection);
                                    break;

                                case "ingameSession" :
                                    log.info("in game");
                                    inGameSessionHandler.inGameHandler(json, connection);
                                    break;

                                case "ingame":
                                    log.info("game data");
                                    gameDataHandler.gameDataHandler(json);
                                    break;
                            }
                        });

                        return outbound.sendString(Mono.just("success"));  // echoing
                    } catch (JSONException e) {
                        log.error("ERROR : {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .then();                                        // 4) complete reactive sequence

    }
}
