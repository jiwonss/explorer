package com.explorer.chat.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class MessageConverter {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static JavaTimeModule javaTimeModule = new JavaTimeModule();

    public static JSONObject convert(Object o) {
        objectMapper.registerModule(javaTimeModule);
        try {
            String result = objectMapper.writeValueAsString(o);
            return new JSONObject(result);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
