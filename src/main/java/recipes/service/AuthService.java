package recipes.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import recipes.dto.AuthenticationResponse;
import recipes.dto.LoginRequest;
import recipes.model.UserEntity;
import recipes.repository.UserRepository;
import recipes.security.JwtProvider;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    private final JwtProvider JwtProvider;

    public UserEntity saveUser(UserEntity userEntity) {
        return this.userRepository.save(userEntity);
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
