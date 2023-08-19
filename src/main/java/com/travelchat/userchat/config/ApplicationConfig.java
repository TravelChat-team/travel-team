package com.travelchat.userchat.config;



import com.travelchat.userchat.repository.UserChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final CustomAuthenticationProvider authProvider;

  private final UserChatRepository userChatRepository;
  private final PasswordEncoder passwordEncoder;


  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userChatRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

//  @Bean
//  public AuthenticationProvider authenticationProvider() {
//    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//    authProvider.setUserDetailsService(userDetailsService());
//    authProvider.setPasswordEncoder(passwordEncoder());
//    return authProvider;
//  }




}
