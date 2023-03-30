package recipes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AppUtilsConfiguration {

    @Bean
    public Set<String> JwtBlacklist() {
        return new HashSet<>();
    }

}
