package hu.bb.travellingappauth.helper;

import hu.bb.travellingappauth.model.JwtClaim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Component
public class AuthUtil {

    private PasswordEncoder passwordEncoder;

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String TWO_FACTOR = "twoFactor";

    @Autowired
    public AuthUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create claims
     * */
    public Map<String,Object> createClaims(JwtClaim jwtClaim){
        Map<String,Object> claims = new HashMap<>();

        if(jwtClaim.getId()!=null)
            claims.put(ID,jwtClaim.getId());

        if(jwtClaim.getEmail()!=null)
            claims.put(EMAIL,jwtClaim.getEmail());

        if(jwtClaim.getTwoFactor()!=null)
            claims.put(TWO_FACTOR,jwtClaim.getTwoFactor());

        return claims;
    }

    /**
     * Encode password
     * */
    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    /**
     * Compare two password
     * */
    public Boolean checkPasswordIsMatch(String passwordFromDb, String passwordFromInput){
        return passwordEncoder.matches(passwordFromInput,passwordFromDb);
    }

    /**
     * Generate random int number
     * */
    public int getRandomNumberUsingInts(int min, int max) throws NoSuchAlgorithmException {
        Random rand = SecureRandom.getInstanceStrong();
        return rand.ints(min, max)
                .findFirst()
                .getAsInt();
    }

    /**
     * Check is any null value in object
     * */
    public Boolean anyNullObjectValue(Object target) throws IllegalAccessException {
        for (Field field : target.getClass().getDeclaredFields())
        {
            field.setAccessible(true);

            if(field.get(target) == null) {
                return true;
            }

            field.setAccessible(false);
        }
        return false;
    }
}
