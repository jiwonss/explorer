package com.explorer.realtime.sessionhandling.repository;

import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMongoRepository extends MongoRepository<Channel, String> {
}
