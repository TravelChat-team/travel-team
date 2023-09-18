package com.travelchat.userchat.controller;



import com.travelchat.userchat.auth.DeleteRequest;
import com.travelchat.userchat.auth.RegistrationRequest;
import com.travelchat.userchat.dto.UserChatDto;
import com.travelchat.userchat.repository.UserChatRepository;
import com.travelchat.userchat.models.Role;
import com.travelchat.userchat.models.UserChat;
import com.travelchat.userchat.service.Impl.UserChatServiceImpl;
import com.travelchat.userchat.service.Impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;



@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserChatController {
        private final UserChatRepository userChatRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserChatServiceImpl userchatService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/list")
//    @GetMapping()
    public List<UserChatDto> list() {
        List<UserChatDto> chatList = userchatService.getAllUserchat();

        log.info("chatList {}", chatList);

        return chatList;
    }

    @PostMapping()
    public UserChat createUserChat(@RequestBody RegistrationRequest userchatRegDto) {
        UserChat userChat = UserChat.builder()
                .username(userchatRegDto.getEmail())
                .email(userchatRegDto.getEmail())
                .password("{bcrypt}" + bCryptPasswordEncoder.encode(userchatRegDto.getPassword()))
                .locale(null)
                .role(Role.USER)
                .lastVisit(LocalDateTime.now())
                .build();
        // Assuming you have a list of role names in the UserchatRegDto, you can add them to the userChat entity
        UserChat userChatSaved = userChatRepository.save(userChat);

        log.info("userChatSaved {}", userChatSaved);


        log.info("userChatSaved2 {}", userChatSaved);
        return userChatRepository.findById(userChatSaved.getId()).get();
    }


    //TODO change authentication after develop
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUserChat(@RequestBody DeleteRequest deleteRequest) {
        var byEmail = userchatService.getByEmail(deleteRequest.getEmail());
        userChatRepository.delete(byEmail);
        // Here, you would typically perform the logic to delete the UserChat with the given id
        // For the sake of this example, we'll assume the deletion is successful

        // You can add your deletion logic here, e.g., userChatService.deleteUserChat(id);

        return ResponseEntity.ok("UserChat #" + deleteRequest.getEmail() + " has been deleted.");
    }

//    @GetMapping("/{id}")
//    public UserChatDto getUserChatWithMessages(@PathVariable("id")  Long id) throws InterruptedException {
//        UserChat userChat = userChatRepository.findById(id).orElseThrow();
//        UserChatDto userChatDto = UserChatDto.builder()
//                .id(userChat.getId())
//                .name(userChat.getUsername())
//                .build();
//        log.info("waiting {}ms", DELAY);
////        Thread.sleep(DELAY += 50);
//        log.info("responding with error");
//        return userChatDto;
////        throw new RuntimeException("Unexpected error");
//    }
}
