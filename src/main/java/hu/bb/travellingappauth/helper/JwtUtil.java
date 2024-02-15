package hu.bb.travellingappauth.helper;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String SECRET = "2D4A614E645267556B58703273357638792F423F4428472B4B6250655368566D";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days


    /**
     * Generate JWT token
     * */
    public String generateToken(String username, Map<String,Object> claims) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey())
                .setClaims(claims)
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
        return new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
