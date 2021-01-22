package com.co.dlp.login.registration;

import com.co.dlp.login.appuser.User;
import com.co.dlp.login.appuser.UserRole;
import com.co.dlp.login.appuser.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("email not valid");
        }
        return userService.signUpUser(new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                UserRole.USER));
    }
}
