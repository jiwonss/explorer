package com.explorer.realtime.sessionhandling.ingame.repository;

import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMongoRepository extends ReactiveMongoRepository<Channel, String> {
}
