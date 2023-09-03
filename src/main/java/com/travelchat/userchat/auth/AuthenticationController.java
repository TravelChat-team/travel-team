package com.travelchat.userchat.auth;

import com.travelchat.userchat.auth.oauth.CustomOAuth2User;
import com.travelchat.userchat.service.Impl.UserChatServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {


    private final AuthenticationService authenticationService;
    private final UserChatServiceImpl userChatServiceImpl;


    public AuthenticationController(@Lazy AuthenticationService authenticationService, UserChatServiceImpl userChatServiceImpl) {
        this.authenticationService = authenticationService;
        this.userChatServiceImpl = userChatServiceImpl;
    }


    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegistrationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/oAuthAuthenticate")
    public ResponseEntity<AuthenticationResponse> oAuthAuthenticate(
           Authentication authentication)  {
         CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
         String oauth2ClientName = oauth2User.getOauth2ClientName();
         String username = oauth2User.getEmail();

         userChatServiceImpl.updateAuthenticationType(username, oauth2ClientName);
         System.out.println("username = " + username);
         AuthenticationResponse authenticationResponse = authenticationService.authenticateOAuth(username);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

}
