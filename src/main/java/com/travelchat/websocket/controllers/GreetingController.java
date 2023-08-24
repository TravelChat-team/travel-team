package com.travelchat.websocket.controllers;


import com.travelchat.websocket.domain.Greeting;
import com.travelchat.websocket.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController implements WebSocketController{
    @Override
    public Greeting greeting(Message message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello write,    " + HtmlUtils.htmlEscape(message.getText()) + "!");
    }

    @Override
    public Greeting chatMessage(String topic, String username, Message message) throws Exception {
        Thread.sleep(1000); // Simulated delay
        return new Greeting(username + ": " + HtmlUtils.htmlEscape(message.getText()));
    }

}
