package com.explorer.realtime.sessionhandling.repository;

import com.explorer.realtime.sessionhandling.ingame.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

}
