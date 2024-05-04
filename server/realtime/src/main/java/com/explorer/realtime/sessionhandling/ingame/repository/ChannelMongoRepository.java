package com.explorer.realtime.sessionhandling.ingame.repository;

import com.explorer.realtime.sessionhandling.ingame.document.Channel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChannelMongoRepository extends ReactiveMongoRepository<Channel, String> {
}
