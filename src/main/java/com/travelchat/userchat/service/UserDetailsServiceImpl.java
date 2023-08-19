package com.travelchat.userchat.service;



import com.travelchat.userchat.auth.AuthenticationType;
import com.travelchat.userchat.repository.UserChatRepository;
import com.travelchat.userchat.token.ConfirmationToken;
import com.travelchat.userchat.token.ConfirmationTokenService;
import com.travelchat.userchat.userchat.Role;
import com.travelchat.userchat.userchat.UserChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.travelchat.userchat.userchat.Constants.ADMIN_PASSWORD;
import static com.travelchat.userchat.userchat.Constants.ADMIN_USERNAME;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserChatRepository userChatRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final ConfirmationTokenService confirmationTokenService;

	private static final String USER_NOT_FOUND = "Could not find user with email %s ";

	@Value(value = "${" + ADMIN_USERNAME + "}")
	private String username;

	@Value(value = "${" + ADMIN_PASSWORD + "}")
	private String password;


	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		
		return userChatRepository.findByEmail(email)
				.orElseThrow(() ->
						new UsernameNotFoundException(
								String.format(USER_NOT_FOUND, email)));
	}

	public void processOAuthPostLogin(String username) {
		UserChat existUserChat = userChatRepository.findByEmailFetchRoes(username);

		if (existUserChat == null) {
			UserChat newUserChat = new UserChat();
			newUserChat.setUsername(username);
			newUserChat.setEmail(username);
			newUserChat.setAuthType(AuthenticationType.GOOGLE);
			newUserChat.setRole(Role.USER);
			newUserChat.setEnabled(true);

			userChatRepository.save(newUserChat);

			System.out.println("Created new userChat: " + username);
		}
	}

	public String signUpUser(UserChat userChat) {
		boolean userExists = userChatRepository
				.findByEmail(userChat.getEmail())
				.isPresent();

		if (userExists) {
			// TODO check of attributes are the same and
			// TODO if email not confirmed send confirmation email.

			return "Email already taken";
		}

		String encodedPassword = bCryptPasswordEncoder
				.encode(userChat.getPassword());

		userChat.setPassword(encodedPassword);

		userChatRepository.save(userChat);

		String token = UUID.randomUUID().toString();

		ConfirmationToken confirmationToken = new ConfirmationToken(
				token,
				LocalDateTime.now(),
				LocalDateTime.now().plusMinutes(15),
				userChat
		);

		confirmationTokenService.saveConfirmationToken(
				confirmationToken);

//        TODO: SEND EMAIL

		return token;
	}

	public int enableUserChat(String email) {
		return userChatRepository.enableUserChat(email);
	}
}
