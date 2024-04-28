package com.explorer.realtime.sessionhandling.repository;

import com.explorer.realtime.sessionhandling.entity.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {

}
