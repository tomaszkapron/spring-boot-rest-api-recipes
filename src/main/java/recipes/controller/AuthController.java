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
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserEntity userEntity) {
        if (authService.existsByEmail(userEntity.getEmail())) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        authService.saveUser(userEntity);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody UserEntity loginRequest) {
         return authService.login(loginRequest);
    }

    @PostMapping("/actuator/shutdown")
    public void shutdown() {}
}
