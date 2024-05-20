package com.explorer.realtime.staticdatahandling.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "labEfficiencies")
public class LabEfficiency {

    @Id
    private String _id;

    private int level;

    private float efficiency;

    public static LabEfficiency from (int level, float efficiency) {
        return LabEfficiency.builder()
                .level(level)
                .efficiency(efficiency)
                .build();
    }


}
