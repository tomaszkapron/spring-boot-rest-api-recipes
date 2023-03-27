package recipes.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import recipes.dto.AuthenticationResponse;
import recipes.dto.LoginRegisterRequest;
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
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserEntity loginRequest) {
         return authService.login(loginRequest);
    }

    @PostMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        return authService.verifyAccount(token);
    }

    @PostMapping("/actuator/shutdown")
    public void shutdown() {}
}
