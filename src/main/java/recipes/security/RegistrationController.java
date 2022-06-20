package recipes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegistrationController {
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @PostMapping("/api/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserEntity userEntity) {
        if (userService.existsByEmail(userEntity.getEmail())) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        userService.saveUser(userEntity);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/actuator/shutdown")
    public void shutdown() {}
}
