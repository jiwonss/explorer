package com.explorer.realtime.sessionhandling.ingame.repository;

import com.explorer.realtime.sessionhandling.ingame.entity.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {

}
