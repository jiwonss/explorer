package com.explorer.realtime.sessionhandling.ingame.repository;

import com.explorer.realtime.sessionhandling.ingame.document.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlayerMongoRepository extends ReactiveMongoRepository<Player, String> {

}
