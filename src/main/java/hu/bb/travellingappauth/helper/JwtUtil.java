package hu.bb.travellingappauth.helper;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.KeyRequest;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_TIME = 864_000_000; // 10 days


    /**
     * Generate JWT token
     * */
    public String generateToken(String username, Map<String,Object> claims) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey())
                .claims(claims)
                .compact();
    }

    /**
     * Validate JWT token
     * */
    public Boolean validate(String token){
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();

        try {
            jwtParser.parse(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Get sign key
     * */
    private SecretKeySpec getKey(){
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }
}
