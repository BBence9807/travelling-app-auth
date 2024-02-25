package hu.bb.travellingappauth;

import hu.bb.travellingappauth.helper.AuthUtil;
import hu.bb.travellingappauth.model.JwtClaim;
import hu.bb.travellingappauth.model.UserLoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.util.Map;


public class AuthUtilTest {

    private static AuthUtil authUtil;
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String TWO_FACTOR = "twoFactor";

    @BeforeAll
    static void init(){
        authUtil = new AuthUtil(new BCryptPasswordEncoder());
    }


    @Test
    public void createClaimsAllTest(){
        Map<String, Object> claims = authUtil.createClaims(JwtClaim.builder().id(1L).email("test@test.hu").twoFactor(true).build());

        Boolean actual = claims.get(ID) != null && claims.get(EMAIL) != null && claims.get(TWO_FACTOR) != null;

        Assertions.assertEquals(true,actual);
    }

    @Test
    public void createClaimsCheckIdNegativeTest(){
        Map<String, Object> claims = authUtil.createClaims(JwtClaim.builder().email("test@test.hu").twoFactor(true).build());

        Boolean actual = claims.get(ID) != null;

        Assertions.assertEquals(false,actual);
    }

    @Test
    public void createClaimsCheckEmailNegativeTest() {
        Map<String, Object> claims = authUtil.createClaims(JwtClaim.builder().id(1L).twoFactor(true).build());

        Boolean actual = claims.get(EMAIL) != null;

        Assertions.assertEquals(false,actual);
    }

    @Test
    public void createClaimsCheckTwoFactorNegativeTest() {
        Map<String, Object> claims = authUtil.createClaims(JwtClaim.builder().id(1L).email("test@test.hu").build());

        Boolean actual = claims.get(TWO_FACTOR) != null;

        Assertions.assertEquals(false,actual);
    }

    @Test
    public void encodePasswordTest(){
        String encoded = authUtil.encodePassword("Test");

        Boolean actual = encoded != null;

        Assertions.assertEquals(true,actual);
    }

    @Test
    public void checkPasswordIsMatchTest(){
        String encoded = authUtil.encodePassword("Test");

        Boolean actual = authUtil.checkPasswordIsMatch(encoded, "Test");

        Assertions.assertEquals(true,actual);
    }

    @Test
    public void checkPasswordIsMatchNegativeTest(){
        String encoded = authUtil.encodePassword("Test");

        Boolean actual = authUtil.checkPasswordIsMatch(encoded, "Test1");

        Assertions.assertEquals(false,actual);
    }

    @Test
    public void getRandomNumberUsingIntsTest() throws NoSuchAlgorithmException {
        Integer min = 0;
        Integer max = 10;

        Integer random = authUtil.getRandomNumberUsingInts(min,max);

        Boolean actual = random != null;

        Assertions.assertEquals(true,actual);

    }

    @Test
    public void getRandomNumberUsingIntsIntervalTest() throws NoSuchAlgorithmException {
        Integer min = 0;
        Integer max = 10;

        Integer random = authUtil.getRandomNumberUsingInts(min,max);

        Boolean actual = random>=min && random<=max;

        Assertions.assertEquals(true,actual);

    }

    @Test
    public void anyNullObjectValueNoNullTest() throws IllegalAccessException {
        Boolean actual = authUtil.anyNullObjectValue(UserLoginRequest.builder().email("Test").password("Test").build());
        Assertions.assertEquals(false,actual);
    }

    @Test
    public void anyNullObjectValueHasNullTest() throws IllegalAccessException {
        Boolean actual = authUtil.anyNullObjectValue(UserLoginRequest.builder().email("Test").build());
        Assertions.assertEquals(true,actual);
    }

}
