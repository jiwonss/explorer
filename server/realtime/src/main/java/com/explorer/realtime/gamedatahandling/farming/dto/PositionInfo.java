package com.explorer.realtime.gamedatahandling.farming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

import java.util.StringJoiner;

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

    public static PositionInfo of(JSONObject json) {
        return PositionInfo.builder()
                .posX(json.getFloat("posX"))
                .posY(json.getFloat("posY"))
                .posZ(json.getFloat("posZ"))
                .rotX(json.getFloat("rotX"))
                .rotY(json.getFloat("rotY"))
                .rotZ(json.getFloat("rotZ"))
                .build();
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add(String.valueOf(posX)).add(String.valueOf(posY)).add(String.valueOf(posZ))
                .add(String.valueOf(rotX)).add(String.valueOf(rotY)).add(String.valueOf(rotZ));
        return stringJoiner.toString();
    }

}
