package com.explorer.realtime.gamedatahandling.logicserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class ToLogicServer {

    public Mono<String> sendRequestToHttpServer(String requestData, String HTTP_SERVER_URL) {

        ByteBuf byteBuf = Unpooled.wrappedBuffer(requestData.getBytes());
        log.info("sendRequestToHttpServer start...");

        return HttpClient
                .create()                   // HTTP 클라이언트 생성
                .post()
                .uri(HTTP_SERVER_URL)       // 요청을 보낼 대상 서버 URL 설정
                .send(Mono.just(byteBuf))
                .responseContent()          // 응답 내용을 stream 형태로 수신
                .aggregate()                // 여러 부분으로 나누어진 응답 컨텐츠를 하나의 전체로 수집한다
                .asString();                // 수집된 컨텐츠를 문자열로 변환하여 반환한다
    }
}
