package recipes.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipes.dto.AuthenticationResponse;
import recipes.model.UserEntity;
import recipes.service.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@NoArgsConstructor
@AllArgsConstructor
public class AuthController {
    @Autowired
    PasswordEncoder encoder;
    @Autowired3
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserEntity userEntity) {
        return authService.saveUser(userEntity);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserEntity loginRequest) {
         return authService.login(loginRequest);
    }

    @PostMapping("/actuator/shutdown")
    public void shutdown() {}
}
