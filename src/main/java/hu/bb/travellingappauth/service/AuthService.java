package hu.bb.travellingappauth.service;

import hu.bb.travellingappauth.helper.JwtUtil;
import hu.bb.travellingappauth.model.*;
import hu.bb.travellingappauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenValidation tokenValidation;

    @Autowired
    private TwoFactorService twoFactorService;

    @Value( "${forgot.password.url}" )
    private String forgotPwUrl;


    private final String EMPTY_DATA_ERROR = "Please provide all needed data! There are required!";
    private final String NOT_EXIST_EMAIL = "Email does not exist! Please provide valid email address!";
    private final String EXIST_EMAIL = "Email does already exist! Please provide valid email address!";
    private final String WRONG_PASSWORD = "Wrong password! Please provide valid password!";
    private final String SUCCESS_PASSWORD_RESET = "Password reset successfully!";
    private final String SUCCESS_TWO_FACTOR_CHANGED = "Two Factor Authorization changed successfully!";
    private final String UNAUTHORIZED = "Permission denied! Invalid token!";
    private final String EMAIL_SEND = "Email was send to given address!";

    private final String ISSUER = "TravellingApp";

    /**
     * Login process workflow
     * */
    public ResponseEntity<String> login(UserLoginRequest userLoginRequest){
        try {
            //Request body null validation
            if(userLoginRequest.getEmail()==null || userLoginRequest.getPassword() == null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Email exist checking
            if(!userRepository.existsByEmail(userLoginRequest.getEmail()))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.UNAUTHORIZED);

            //Get user from database by email address
            User user = userRepository.getUserByEmail(userLoginRequest.getEmail());

            //Password correct check
            if(!checkPasswordIsMatch(user.getPassword(),userLoginRequest.getPassword()))
                return new ResponseEntity<>(this.WRONG_PASSWORD, HttpStatus.UNAUTHORIZED);

            //JWT token generálása
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",user.getId());
            claims.put("email",user.getEmail());
            claims.put("twoFactor",user.getTwoFactor());
            return new ResponseEntity<>(jwtUtil.generateToken(user.getEmail(),claims), HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Register process workflow
     * */
    public ResponseEntity<Object> register(UserRegisterRequest userRegisterRequest){
        try {
            //Request body null validation
            if(userRegisterRequest.getEmail()==null || userRegisterRequest.getPassword() == null || userRegisterRequest.getFirstName() == null || userRegisterRequest.getLastName() == null )
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Email exist checking
            if(userRepository.existsByEmail(userRegisterRequest.getEmail()))
                return new ResponseEntity<>(this.EXIST_EMAIL, HttpStatus.FOUND);

            //Create and save user object
            User user = User.builder()
                    .email(userRegisterRequest.getEmail())
                    .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                    .firstName(userRegisterRequest.getFirstName())
                    .lastName(userRegisterRequest.getLastName())
                    .build();

            return new ResponseEntity<>(userRepository.save(user),HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Compare two password
     * */
    private Boolean checkPasswordIsMatch(String passwordFromDb, String passwordFromInput){
        return passwordEncoder.matches(passwordFromInput,passwordFromDb);
    }

    /**
     * Reset password process workflow
     * */
    public ResponseEntity<String> resetPw(HttpServletRequest request,ResetPasswordRequest resetPasswordRequest){
        try {

            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request body null validation
            if(resetPasswordRequest.getPassword()==null || resetPasswordRequest.getEmail() == null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Get entity by email
            User user = userRepository.getUserByEmail(resetPasswordRequest.getEmail());

            if(user == null)
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

            //Mod user password
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));

            //Save new entity
            userRepository.save(user);

            return new ResponseEntity<>(this.SUCCESS_PASSWORD_RESET,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Forgot password process workflow
     * */
    public ResponseEntity<String> forgotPw(String email){
        try {

            //Request null validation
            if(email == null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(email))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);


            //jwt token összeállítása és url cím összeállítása
            Map<String,Object> claims = new HashMap<>();
            String token = jwtUtil.generateToken(email,claims);

            String url = forgotPwUrl+token;

            //Email kiküldése


            return new ResponseEntity<>(this.EMAIL_SEND,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Set User Two Factor State
     * */
    public ResponseEntity<String> setTwoFactorAuth(HttpServletRequest request, TwoFactorSetRequest twoFactorSetRequest){
        try {
            //Token validation
            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(twoFactorSetRequest.getEmail()==null || twoFactorSetRequest.getValue()==null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(twoFactorSetRequest.getEmail()))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

            //Mod twoFactor in User
            User user = userRepository.getUserByEmail(twoFactorSetRequest.getEmail());
            user.setTwoFactor(twoFactorSetRequest.getValue());

            userRepository.save(user);

            return new ResponseEntity<>(SUCCESS_TWO_FACTOR_CHANGED,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get QR code to set Two Factor
     * */
    public ResponseEntity<Object> getTwoFactorQrCode(HttpServletRequest request, String email){
        try {
            //Token validation
            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(email==null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(email))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

            //Generate Qr Code
            return new ResponseEntity<>(twoFactorService.generateQrCode(email,ISSUER),HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Validate Two Factor Code
     * */
    public ResponseEntity<Object> checkTwoFactorCode(HttpServletRequest request, String code){
        try {
            //Token validation
            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(code==null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Check Two Factor Code
            return new ResponseEntity<>(twoFactorService.checkCode(code),HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get and Set Two Factor Recovery Code
     * */
    public ResponseEntity<String> getTwoFactoryRecovery(HttpServletRequest request, String email){
        try {
            //Token validation
            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(email==null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(email))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);


            //Generate recovery code
            String code = twoFactorService.generateRecovery()[this.getRandomNumberUsingInts(0,twoFactorService.generateRecovery().length-1)];

            //Set recovery code to user
            User user = userRepository.getUserByEmail(email);
            user.setTwoFactorRecovery(code);

            userRepository.save(user);

            //Check Two Factor Code
            return new ResponseEntity<>(code,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate random int number
     * */
    private int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }

    /**
     * Check Factor Recovery Code
     * */
    public ResponseEntity<Object> checkTwoFactoryRecovery(HttpServletRequest request, RecoveryCodeCheckRequest recoveryCodeCheckRequest){
        try {
            //Token validation
            if(!tokenValidation.validate(request))
                return new ResponseEntity<>(this.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(recoveryCodeCheckRequest.getEmail()==null || recoveryCodeCheckRequest.getCode()==null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(recoveryCodeCheckRequest.getEmail()))
                return new ResponseEntity<>(this.NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

            //Check and verify code
            User user = userRepository.getUserByEmail(recoveryCodeCheckRequest.getEmail());

            if(user.getTwoFactorRecovery() != recoveryCodeCheckRequest.getCode())
                return new ResponseEntity<>(false,HttpStatus.OK);

            return new ResponseEntity<>(true,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
