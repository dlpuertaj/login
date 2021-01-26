package com.co.dlp.login.registration;

import com.co.dlp.login.registration.token.ConfirmationToken;
import com.co.dlp.login.registration.token.ConfirmationTokenService;
import com.co.dlp.login.user.User;
import com.co.dlp.login.user.UserRole;
import com.co.dlp.login.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("email not valid");
        }

        String token = userService.signUpUser(new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                UserRole.USER));

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;

        return token;
    }

    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService
                .geToken(token)
                .orElseThrow(()-> new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmefAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());

        return "confirmed";
    }
}
