package com.travelchat.userchat.service.Impl;


import com.travelchat.userchat.auth.AuthenticationService;
import com.travelchat.userchat.auth.AuthenticationType;
import com.travelchat.userchat.dto.UserChatDto;
import com.travelchat.userchat.repository.UserChatRepository;
import com.travelchat.userchat.models.Role;
import com.travelchat.userchat.models.UserChat;
import com.travelchat.userchat.service.UserChatService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.travelchat.userchat.models.Constants.ADMIN_PASSWORD;
import static com.travelchat.userchat.models.Constants.ADMIN_USERNAME;

@Slf4j
@Service
@Transactional
public class UserChatServiceImpl implements UserChatService {

    private final UserChatRepository userChatRepository;
    private final BCryptPasswordEncoder passwordEncoder;




    @Value(value = "${" + ADMIN_USERNAME + "}")
    private String username;

    @Value(value = "${" + ADMIN_PASSWORD + "}")
    private String password;

    public UserChatServiceImpl(UserChatRepository userChatRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userChatRepository = userChatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateAuthenticationType(String username, String oauth2ClientName) {
        UserChat userChat = userChatRepository.findByEmailFetchRoes(username);
        System.out.println("userChat = " + userChat);

        AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
        if (userChat == null) {
            UserChat standartUserChat = UserChat.builder()
                    .username(username)
                    .email(username)
                    .password("")
                    .role(Role.USER)
                    .enabled(true)
                    .authType(authType)
                    .build();
            userChatRepository.save(standartUserChat);
        }


//    	userChatRepository.updateAuthenticationType(username, authType);
    	System.out.println("Updated user's authentication type to " + authType);
    }

    @Override
    public List<UserChatDto> getAllUserchat() {
        List<UserChat> userChatList = userChatRepository.findAll();
        return userChatList.stream()
                .map(UserChatDto::fromUserChat)
                .toList();
    }

    @Override
    public UserChatDto getById(Long id) {
        return null;
    }
}
