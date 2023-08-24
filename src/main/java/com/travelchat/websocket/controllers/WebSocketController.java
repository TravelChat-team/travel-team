package com.travelchat.websocket.controllers;

import com.travelchat.websocket.domain.Greeting;
import com.travelchat.websocket.domain.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

//@MessageMapping("/hello")
public interface WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    Greeting greeting(Message message) throws Exception;

    @MessageMapping("/chat/{topic}/{username}") // Define a dynamic topic and username in the URL
    @SendTo("/private/{topic}/{username}") // Send the message to a private chat
    Greeting chatMessage(@DestinationVariable String topic, @DestinationVariable String username, Message message) throws Exception;

}
