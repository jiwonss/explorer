package com.explorer.realtime.serverManaging;

import com.explorer.realtime.global.broadcasting.Broadcasting;
import com.explorer.realtime.global.broadcasting.Multicasting;
import com.explorer.realtime.global.broadcasting.Unicasting;
import com.explorer.realtime.global.redis.RedisService;
import com.explorer.realtime.global.session.SessionManager;
import com.explorer.realtime.global.teamCode.TeamCodeGenerator;
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

    private final TeamCodeGenerator teamCodeGenerator;
    private final SessionManager sessionManager;
    private final RedisService redisService;
    private final Broadcasting broadcasting;
    private final Multicasting multicasting;
    private final Unicasting unicasting;

    public RequestHandler(TeamCodeGenerator teamCodeGenerator, SessionManager sessionManager, RedisService redisService, Broadcasting broadcasting, Multicasting multicasting, Unicasting unicasting) {
        this.teamCodeGenerator = teamCodeGenerator;
        this.sessionManager = sessionManager;
        this.redisService = redisService;
        this.broadcasting = broadcasting;
        this.multicasting = multicasting;
        this.unicasting = unicasting;
    }

    public Mono<Void> handleRequest(NettyInbound inbound, NettyOutbound outbound) {

        return inbound
                .receive()                                      // 1) receive data
                .asString()                                     // 2) convert data : byte -> string
                .flatMap(msg -> {                               // 3) process data
                    try{

                        JSONObject json = new JSONObject(msg);      // parse string to json
                        log.info("Received Json Data: {}", json);

                        String type = json.getString("type");
                        String event = json.getString("event");

                        switch(type) {
                                case "waitingRoomSession" :

                                    log.info("waiting room");
                                    String uid = json.getString("uid");

                                    switch(event) {
                                        case "createWaitingRoom":
                                            log.info("create Waiting Room");

                                            return teamCodeGenerator.getCode()
                                                    .flatMap(teamCode -> {
                                                        inbound.withConnection(connection -> {
                                                            sessionManager.setConnection(uid, connection);
                                                            redisService.saveUidToTeamCode(teamCode, uid, "waitingRoom").subscribe();
                                                            log.info("uid: {}, teamCode: {}, connection: {}", sessionManager.getUid(connection), teamCode, sessionManager.getConnection(uid));
                                                        });
                                                        return Mono.empty();
                                                    });

                                        case "joinWaitingRoom":
                                            log.info("join Waiting Room");
                                            String teamCode_ = json.getString("teamCode");
                                            inbound.withConnection(connection -> {
                                                sessionManager.setConnection(uid, connection);
                                                redisService.saveUidToTeamCode(teamCode_,uid,"waitingRoom").subscribe();
                                                log.info("uid: {}, teamCode: {}, connection: {}", sessionManager.getUid(connection), sessionManager.getConnection(uid), teamCode_);
                                                unicasting.unicasting(teamCode_, uid, json).subscribe();
                                            });
                                            break;
                                    }
                                    break;

                            case "ingameSession" :

                                log.info("start game");
                                String teamCode = json.getString("teamCode");

                                switch (event) {
                                    case "startGame":
                                        log.info("start game");
                                        broadcasting.broadcasting(teamCode, json).subscribe();
                                        break;

                                    case "restartGame":
                                        log.info("restart game");
                                        String uid_ = json.getString("uid");
                                        multicasting.multicasting(teamCode, uid_, json).subscribe();
                                        break;
                                }
                                break;
                        }

                        return Mono.empty();

                    } catch (JSONException e) {
                        log.error("ERROR : {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .then();                                        // 4) complete reactive sequence

    }
}
