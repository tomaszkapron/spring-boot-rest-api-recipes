package recipes.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import recipes.dto.AuthenticationResponse;
import recipes.model.UserEntity;
import recipes.repository.UserRepository;
import recipes.security.JwtProvider;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider JwtProvider;
    private final PasswordEncoder encoder;

    public ResponseEntity<String> saveUser(UserEntity userEntity) {
        if (existsByEmail(userEntity.getEmail())) {
            return new ResponseEntity<>("User with this email is already registered", HttpStatus.BAD_REQUEST);
        }

        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        this.userRepository.save(userEntity);
        return new ResponseEntity<>( "User registered successfully", HttpStatus.OK);
    }

    public Optional<UserEntity> findByEmail(String email) { return this.userRepository.findByEmail(email); }

    public boolean existsById(Long id) {
        return this.userRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsUserEntityByEmail(email);
    }

    public AuthenticationResponse login(UserEntity loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = JwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getEmail());
    }
}
