package com.explorer.chat.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class CustomJsonSerializer extends GenericJackson2JsonRedisSerializer {

    private ObjectMapper objectMapper;

    public CustomJsonSerializer() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        try {
            if (source == null) {
                return new byte[0];
            }
            return objectMapper.writeValueAsString(source).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        if (source == null || source.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(new String(source, StandardCharsets.UTF_8), Object.class);
        } catch (Exception e) {
            throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
        }
    }
}
