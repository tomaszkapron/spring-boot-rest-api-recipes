package recipes.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import recipes.model.VerificationToken;
import recipes.repository.VerificationTokenRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
    public void save(VerificationToken token) {
        verificationTokenRepository.save(token);
    }
}
