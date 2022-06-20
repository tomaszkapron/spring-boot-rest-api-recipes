package recipes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserEntity saveUser(UserEntity userEntity) {
        return this.userRepository.save(userEntity);
    }

    public UserEntity findByEmail(String email) { return this.userRepository.findByEmail(email); }

    public boolean existsById(Long id) {
        return this.userRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsUserEntityByEmail(email);
    }
}
