package com.explorer.realtime.channeldatahandling.client;

import com.explorer.realtime.channeldatahandling.dto.Message;
import com.explorer.realtime.channeldatahandling.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "api-gateway", url = "${client.api-gateway.url}")
public interface UserClient {

    @GetMapping("/user/users/profile")
    Message<UserInfo> getUserInfo(@RequestHeader("Authorization") String accessToken);

}