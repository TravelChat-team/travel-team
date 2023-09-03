package com.travelchat.userchat.service;

import com.travelchat.userchat.dto.UserChatDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserChatService {
    @GetMapping("/users/list")
    List<UserChatDto> getAllUserchat();

    @GetMapping("/users/{id}")
    @CircuitBreaker(name = "userchats", fallbackMethod = "defaultUserchatDto")
//    @TimeLimiter(name = )
    UserChatDto getById(@PathVariable("id") Long id);
    default  UserChatDto defaultUserchatDto(Long userchatId, Throwable throwable) {
        return new UserChatDto(
                userchatId,
                "HIDDEN",
                "default@email.com",
                "HIDDEN",
                "HIDDEN");
    }
}
