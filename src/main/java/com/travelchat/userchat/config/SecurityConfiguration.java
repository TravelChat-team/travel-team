package com.travelchat.userchat.config;

import com.travelchat.userchat.auth.oauth.CustomOAuth2UserService;
import com.travelchat.userchat.auth.oauth.OAuthLoginSuccessHandler;
import com.travelchat.userchat.repository.UserChatRepository;
import com.travelchat.userchat.service.UserDetailsServiceImpl;
import com.travelchat.userchat.userchat.Permission;
import com.travelchat.userchat.userchat.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity

@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final CustomAuthenticationProvider authenticationProvider;
    private final UserChatRepository userChatRepository;
    private final PasswordEncoder passwordEncoder;
//  private final LogoutHandler logoutHandler;
  private final OAuthLoginSuccessHandler oauthLoginSuccessHandler;
  private final CustomOAuth2UserService oauth2UserService;
  private final UserDetailsServiceImpl userDetailService;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter,
                                 CustomAuthenticationProvider authenticationProvider,
                                 UserChatRepository userChatRepository,
                                 PasswordEncoder passwordEncoder,
                                 @Lazy OAuthLoginSuccessHandler oauthLoginSuccessHandler,
                                 CustomOAuth2UserService oauth2UserService,
                                 UserDetailsServiceImpl userDetailService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.userChatRepository = userChatRepository;
        this.passwordEncoder = passwordEncoder;
        this.oauthLoginSuccessHandler = oauthLoginSuccessHandler;
        this.oauth2UserService = oauth2UserService;
        this.userDetailService = userDetailService;
    }

    @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http

            .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
            .authorizeHttpRequests((authorizeHttpRequests) ->
                    authorizeHttpRequests
                            .requestMatchers(
                "/api/v1/auth/**",
                "/api/v*/auth/registration/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html",
                "/users/list"
        )
          .permitAll()
        .requestMatchers("/api/v1/management/**").hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
        .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(Permission.ADMIN_READ.name(), Permission.MANAGER_READ.name())
        .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(Permission.ADMIN_CREATE.name(), Permission.MANAGER_CREATE.name())
        .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(Permission.ADMIN_UPDATE.name(), Permission.MANAGER_UPDATE.name())
        .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(Permission.ADMIN_DELETE.name(), Permission.MANAGER_DELETE.name())

        .anyRequest()
          .authenticated())
//        .sessionManagement((sessionManagement) ->
//                sessionManagement
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login((login) -> login
                .loginPage("/login")
                .defaultSuccessUrl("/api/v1/demo-controller")
                .permitAll()
                    .userInfoEndpoint(userInfoEndpoint->
                            userInfoEndpoint.userService(oauth2UserService))
                        .successHandler(oauthLoginSuccessHandler))
//            .logout(Customizer.withDefaults())
//        .logout((logout) ->
//                logout.deleteCookies("remove")
//                        .invalidateHttpSession(false)
//                        .logoutUrl("/api/v1/auth/logout")
//                        .logoutSuccessUrl("/")
//                        .permitAll()
//        )

//        .logoutHandler(logoutHandler)
//        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())

            .cors(Customizer.withDefaults())// Enable CORS with default settings
    ;

    return http.build();
  }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Allow all origins
        configuration.addAllowedMethod("*"); // Allow all HTTP methods
        configuration.addAllowedHeader("*"); // Allow all headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }
}
