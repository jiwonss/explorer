package com.explorer.realtime.gamedatahandling.ending.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.ingame.enums.Status;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleGameEnding {

    private final ChannelMongoRepository channelMongoRepository;
    private final Broadcasting broadcasting;

    private static final String eventName = "handleGameEnding";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        String image = json.getString("image");
        log.info("[process] channelId : {}, userId : {}, image : {}", channelId, userId, image);

        return channelMongoRepository.findById(channelId)
                .flatMap(channel -> {
                    log.info("[process] channel : {}", channel);
                    channel.setStatus(Status.ENDED);
                    channel.setImage(image);
                    return channelMongoRepository.save(channel);
                })
                .flatMap(savedChannel -> {
                    log.info("[process] savedChannel : {}", savedChannel);
                    return broadcasting.broadcasting(
                            channelId,
                            MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING))
                    );
                })
                .then();
    }

}
