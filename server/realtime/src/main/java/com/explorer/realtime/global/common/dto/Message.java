package com.explorer.realtime.global.common.dto;

import com.explorer.realtime.global.common.enums.CastingType;
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
        private final String event;
        private final String castingType;
        private final String resultCode;
        private final String resultMessage;

        private static DataHeader success(String event, String castingType) {
            return DataHeader.builder()
                    .msg("success")
                    .event(event)
                    .castingType(castingType)
                    .build();
        }

        private static DataHeader success(String event, String castingType, String resultCode, String resultMessage) {
            return DataHeader.builder()
                    .msg("success")
                    .event(event)
                    .castingType(castingType)
                    .resultCode(resultCode)
                    .resultMessage(resultMessage)
                    .build();
        }

        private static DataHeader fail(String event, String castingType) {
            return DataHeader.builder()
                    .msg("fail")
                    .event(event)
                    .castingType(castingType)
                    .build();
        }

        private static DataHeader fail(String event, String castingType, String resultCode, String resultMessage) {
            return DataHeader.builder()
                    .msg("fail")
                    .event(event)
                    .castingType(castingType)
                    .resultCode(resultCode)
                    .resultMessage(resultMessage)
                    .build();
        }
    }

    public static <T> Message<T> success(String event, CastingType castingType, T dataBody) {
        return Message.<T>builder()
                .dataHeader(DataHeader.success(event, castingType.name()))
                .dataBody(dataBody)
                .build();
    }

    public static <T> Message<T> success(String event, CastingType castingType, String resultCode, String resultMessage, T dataBody) {
        return Message.<T>builder()
                .dataHeader(DataHeader.success(event, castingType.name(), resultCode, resultMessage))
                .dataBody(dataBody)
                .build();
    }

    public static <T> Message<T> success(String event, CastingType castingType) {
        return Message.<T>builder()
                .dataHeader(DataHeader.success(event, castingType.name()))
                .build();
    }

    public static <T> Message<T> fail(String event, CastingType castingType) {
        return Message.<T>builder()
                .dataHeader(DataHeader.fail(event, castingType.name()))
                .dataBody(null)
                .build();
    }


    public static <T> Message<T> fail(String event, CastingType castingType, String resultCode, String resultMessage) {
        return Message.<T>builder()
                .dataHeader(DataHeader.fail(event, castingType.name(), resultCode, resultMessage))
                .dataBody(null)
                .build();
    }

}
