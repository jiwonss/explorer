//package com.explorer.realtime.sessionhandling.ingame;
//
//import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
//import com.explorer.realtime.sessionhandling.ingame.entity.Player;
//import org.bson.types.ObjectId;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Service
//public class StartGamebackup {
//
//    private static final Logger log = LoggerFactory.getLogger(StartGamebackup.class);
//
////    @Autowired
////    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    private RedisTemplate<String, String> redisListTemplate;
//
////    public void saveTeamMembers(String teamCode, List<String> memberIds) {
////        String key = "teamCode:" + teamCode;
////        for (String memberId : memberIds) {
////            redisTemplate.opsForList().rightPush(key, memberId);
////        }
////    }
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    public Mono<Void> process(JSONObject json) {
//        String teamCode = json.getString("teamCode");
//        String channelName = json.getString("channelName");
//
//        ObjectId newChannelId = new ObjectId();
//
//        // redis에서 데이터 넘어오는 구조 key - teamCode:"teamcode" 1 2 3 4
//        // 리스트 형태로 넘어온다.
//        List<String> memberIds = redisListTemplate.opsForList().range("teamCode:" + teamCode, 0, -1);
//
//        // 해시 데이터 구조를 생각했는데 redis에서 받아온 정보를 mongodb에 저장하지 못했다.
////        Map<Object, Object> playerList = redisTemplate.opsForHash().entries(teamCode);
//
////        List<Long> memberIds = new ArrayList<>();
////        playerList.forEach((k, v) -> memberIds.add(Long.parseLong((String) v)));
//        List<Long> memberIdsLong = new ArrayList<>();
//        for (Object memberId : memberIds) {
//            memberIdsLong.add(Long.parseLong((String) memberId));
//        }
//
//        Channel channel = new Channel(newChannelId.toHexString(), channelName, memberIdsLong);
//        mongoTemplate.save(channel);
//
////        playerList.forEach((k, v) -> {
////            Player player = new Player();
////            player.setId(Objects.toString(k));
////            player.setUserId(Long.parseLong((String) v));
////            player.setChannelId(teamCode);
////            mongoTemplate.save(player);
////        });
//        for (String memberId : memberIds) {
//            Player player = new Player();
////            player.setId(memberId);
//            player.setUserId(Long.parseLong(memberId));
//            player.setChannelId(newChannelId.toHexString());
//            mongoTemplate.save(player);
//        }
//
//        log.info("CreateGame");
//        return outbound.sendString(Mono.just("Game created with ID: " + teamCode)).then();
//    }
//}
//
//// redis 팀코드를 채널 아이디로 업데이트
//// 게임 시작 시 redis의 userId가 key인 값을 참조하여 avatar와 nickname가져오기
//// 필요 없고 클라이언트한테 바로 redis 정보 보내주기
