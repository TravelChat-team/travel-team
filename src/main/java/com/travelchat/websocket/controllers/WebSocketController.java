package com.travelchat.websocket.controllers;

import com.travelchat.websocket.domain.Greeting;
import com.travelchat.websocket.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

//@MessageMapping("/hello")
public interface WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    Greeting greeting(Message message) throws Exception;

}
