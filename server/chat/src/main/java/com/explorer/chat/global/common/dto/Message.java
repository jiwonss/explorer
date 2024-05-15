package com.explorer.chat.global.common.dto;

import com.explorer.chat.global.common.enums.CastingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message<T> {

    private final DataHeader dataHeader;
    private final T dataBody;

    @Getter
    @Builder
    @AllArgsConstructor
    private static class DataHeader {

        private final String msg;
        private final String eventName;
        private final String castingType;
        private final String resultCode;
        private final String resultMessage;

        private static DataHeader success(String eventName, String castingType) {
            return DataHeader.builder()
                    .msg("success")
                    .eventName(eventName)
                    .castingType(castingType)
                    .build();
        }

        private static DataHeader fail(String eventName, String castingType) {
            return DataHeader.builder()
                    .msg("fail")
                    .eventName(eventName)
                    .castingType(castingType)
                    .build();
        }
    }

    public static <T> Message<T> success(String eventName, CastingType castingType, T dataBody) {
        return Message.<T>builder()
                .dataHeader(DataHeader.success(eventName, castingType.name()))
                .dataBody(dataBody)
                .build();
    }

    public static <T> Message<T> fail(String eventName, CastingType castingType, T dataBody) {
        return Message.<T>builder()
                .dataHeader(DataHeader.fail(eventName, castingType.name()))
                .dataBody(dataBody)
                .build();
    }

}
