package com.travelchat.userchat.dto;

import com.travelchat.userchat.models.UserChat;
import lombok.Data;

@Data
public class UserChatDto {
    private Long id;
    private String name;
    private String email;
    private String firstName;
    private String lastName;

    public UserChatDto(Long id, String name, String email, String firstName, String lastName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Static method to map UserChat to UserChatDto
    public static UserChatDto fromUserChat(UserChat userChat) {
        return new UserChatDto(
                userChat.getId(),
                userChat.getFirstName() + " " + userChat.getLastName(),
                userChat.getEmail(),
                userChat.getFirstName(),
                userChat.getLastName()
        );
    }
}