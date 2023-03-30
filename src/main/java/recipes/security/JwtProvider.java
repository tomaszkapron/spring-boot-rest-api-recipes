package recipes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;q

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import recipes.dto.UserDetailsImpl;
import recipes.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import recipes.service.UserDetailsServiceImpl;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Service
public class JwtProvider {
    @Value("${jwt.secret.key}")
    private String secretKeyPath;
    private String secretKey;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    Set<String> blacklist;

    @PostConstruct
    protected void init() {
        secretKey = readSecretKey(secretKeyPath);
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private String readSecretKey(String secretKeyPath) {
        String content;
        try {
            File file = ResourceUtils.getFile(secretKeyPath);
            content = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Could not read secret key from file");
        }
        return content;
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl)authentication.getPrincipal();

        Claims claims = Jwts.claims().setSubject(principal.getUsername());

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpirationInMillis);

        return Jwts.builder().
                setClaims(claims).
                setIssuedAt(now).
                setExpiration(validity).
                setSubject(principal.getUsername()).
                signWith(SignatureAlgorithm.HS256, secretKey).
                compact();
    }

    public boolean validateToken(String token) {
        if (blacklist.contains(token)) {
            throw new UnauthorizedException("Token is blacklisted, login again");
        }
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getTokenFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object credentials = authentication.getCredentials();
        if (credentials instanceof String) {
            return (String) credentials;
        }
        return null;
    }
}
