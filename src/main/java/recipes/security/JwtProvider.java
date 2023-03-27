package recipes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import recipes.dto.UserDetailsImpl;
import recipes.model.UserEntity;
import io.jsonwebtoken.Jwts;
import recipes.service.UserDetailsServiceImpl;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

@Service
public class JwtProvider {

    private Key key;
    @Value("${jwt.secret.key}")
    private String secretKeyPath;
    private String secretKey;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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
}
