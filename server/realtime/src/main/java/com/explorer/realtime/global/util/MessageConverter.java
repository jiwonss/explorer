package com.explorer.realtime.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class MessageConverter {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static JSONObject convert(Object o) {
        try {
            String result = objectMapper.writeValueAsString(o);
            return new JSONObject(result);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
