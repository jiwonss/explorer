package com.explorer.realtime.sessionhandling.waitingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PositionInfo {

    private float posX;
    private float posY;
    private float posZ;
    private float rotX;
    private float rotY;
    private float rotZ;

    public static PositionInfo of(float posX, float posY, float posZ,
                            float rotX, float rotY, float rotZ) {
        return PositionInfo.builder()
                .posX(posX)
                .posY(posY)
                .posZ(posZ)
                .rotX(rotX)
                .rotY(rotY)
                .rotZ(rotZ)
                .build();
    }

}
