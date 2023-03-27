package recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import recipes.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    boolean existsByToken(String token);
}
