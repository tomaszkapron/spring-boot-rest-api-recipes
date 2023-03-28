package recipes.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import recipes.dto.AuthenticationResponse;
import recipes.dto.LoginRegisterRequest;
import recipes.exception.ResourceNotFoundException;
import recipes.exception.UnauthorizedException;
import recipes.model.NotificationEmail;
import recipes.model.UserEntity;
import recipes.model.VerificationToken;
import recipes.repository.UserRepository;
import recipes.security.JwtProvider;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider JwtProvider;
    private final PasswordEncoder encoder;
    private final MailService mailService;
    private final Set<String> blacklist;


    public ResponseEntity<String> registerUser(LoginRegisterRequest registerRequest) {
        if (existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>("User with this email is already registered", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = UserEntity.builder()
                .email(registerRequest.getEmail())
                .password(encoder.encode(registerRequest.getPassword()))
                .created(Instant.now())
                .enabled(false)
                .build();

        this.userRepository.save(user);

        String registrationToken = generateVerificationToken(user);
        mailService.sendMail(prepareActivationMail(user.getEmail(), registrationToken));

        return new ResponseEntity<>( "User registered successfully", HttpStatus.OK);
    }

    private NotificationEmail prepareActivationMail(String userEmail, String registrationToken) {
        return new NotificationEmail("Activate your account", userEmail,
                        "please click on the below url to activate your account : " +
                        "http://localhost:8881/api/auth/accountVerification/" + registrationToken);
    }

    private String generateVerificationToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS));

        verificationTokenService.save(verificationToken);
        return token;
    }

    public Optional<UserEntity> findByEmail(String email) { return this.userRepository.findByEmail(email); }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsUserEntityByEmail(email);
    }

    public AuthenticationResponse login(UserEntity loginRequest) {
        UserEntity user = findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User with this email is not registered"));
        if (!user.isEnabled()) {
            throw new UnauthorizedException("User is not activated");
        }
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = JwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getEmail());
    }

    public ResponseEntity<String> verifyAccount(String token) {
        Optional<VerificationToken> byToken = verificationTokenService.findByToken(token);
        VerificationToken verificationToken = byToken.orElse(null);
        if (verificationToken == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    public void logout(Authentication authentication) {
        String token = JwtProvider.getTokenFromAuthentication(authentication);
        blacklist.add(token);
        SecurityContextHolder.clearContext();
    }
}
