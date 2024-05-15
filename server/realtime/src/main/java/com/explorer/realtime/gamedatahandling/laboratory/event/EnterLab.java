package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.UseLaboratoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnterLab {

    private final UseLaboratoryRepository useLaboratoryRepository;

    public Mono<Void> process(JSONObject json) {

        log.info("EnterLab process start...");

        /*
         * 1) 해당 연구소를 사용하고 있는 사람이 있는지 확인
         */
        return findPlayersUsingLaboratory(json)
                .flatMap(users -> {
                    if(users.isEmpty()) {
                        log.info("No one is currently using the laboratory");
                    } else {
                        log.warn("Laboratory is currently being used by: {}", users);
                    }
                    return Mono.empty();
                })
                .then();

    }

    private Mono<Map<Object, Object>> findPlayersUsingLaboratory(JSONObject json) {
        return useLaboratoryRepository.findAll(json);
    }
}
