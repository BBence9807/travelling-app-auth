package hu.bb.travellingappauth.service;

import hu.bb.travellingappauth.helper.AuthUtil;
import hu.bb.travellingappauth.helper.JwtUtil;
import hu.bb.travellingappauth.model.*;
import hu.bb.travellingappauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenValidation tokenValidation;

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthUtil authUtil;

    @Value( "${forgot.password.url}" )
    private String forgotPwUrl;


    private static final String EMPTY_DATA_ERROR = "Please provide all needed data! There are required!";
    private static final String NOT_EXIST_EMAIL = "Email does not exist! Please provide valid email address!";
    private static final String EXIST_EMAIL = "Email does already exist! Please provide valid email address!";
    private static final String WRONG_PASSWORD = "Wrong password! Please provide valid password!";
    private static final String SUCCESS_PASSWORD_RESET = "Password reset successfully!";
    private static final String SUCCESS_TWO_FACTOR_CHANGED = "Two Factor Authorization changed successfully!";
    private static final String UNAUTHORIZED = "Permission denied! Invalid token!";
    private static final String EMAIL_SEND = "Email was send to given address!";

    private static final String ISSUER = "TravellingApp";

    private static final String EMAIL_SUBJECT="Travelling Guide App Forgot Password";
    private static final String EMAIL_TEXT="Please click the link to set new password: ";


    /**
     * Login process workflow
     * */
    public ResponseEntity<String> login(UserLoginRequest userLoginRequest){
        try {
            //Request body null validation
            if(authUtil.anyNullObjectValue(userLoginRequest))
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Email exist checking
            if(Boolean.FALSE.equals(userRepository.existsByEmail(userLoginRequest.getEmail())))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.UNAUTHORIZED);

            //Get user from database by email address
            User user = userRepository.getUserByEmail(userLoginRequest.getEmail());

            //Password correct check
            if(!authUtil.checkPasswordIsMatch(user.getPassword(),userLoginRequest.getPassword()))
                return new ResponseEntity<>(WRONG_PASSWORD, HttpStatus.UNAUTHORIZED);

            //JWT token generálása
            return new ResponseEntity<>(jwtUtil.generateToken(user.getEmail(),
                    authUtil.createClaims(JwtClaim.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .twoFactor(user.getTwoFactor())
                    .build())), HttpStatus.OK);

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
            if(authUtil.anyNullObjectValue(userRegisterRequest))
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Email exist checking
            if(Boolean.TRUE.equals(userRepository.existsByEmail(userRegisterRequest.getEmail())))
                return new ResponseEntity<>(EXIST_EMAIL, HttpStatus.FOUND);

            //Create and save user object

            return new ResponseEntity<>(userRepository.save(User.builder()
                    .email(userRegisterRequest.getEmail())
                    .password(authUtil.encodePassword(userRegisterRequest.getPassword()))
                    .firstName(userRegisterRequest.getFirstName())
                    .lastName(userRegisterRequest.getLastName())
                    .build()),HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Reset password process workflow
     * */
    public ResponseEntity<String> resetPw(HttpServletRequest request,ResetPasswordRequest resetPasswordRequest){
        try {

            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request body null validation
            if(authUtil.anyNullObjectValue(resetPasswordRequest))
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            //Get entity by email
            User user = userRepository.getUserByEmail(resetPasswordRequest.getEmail());

            if(user == null)
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

            //Mod user password
            user.setPassword(authUtil.encodePassword(resetPasswordRequest.getPassword()));

            //Save new entity
            userRepository.save(user);

            return new ResponseEntity<>(SUCCESS_PASSWORD_RESET,HttpStatus.OK);

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
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(Boolean.TRUE.equals(userRepository.existsByEmail(email)))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);


            //jwt token összeállítása és url cím összeállítása
            String token = jwtUtil.generateToken(email,authUtil.createClaims(JwtClaim.builder().email(email).build()));

            String url = forgotPwUrl+token;

            //Email kiküldése
            //emailService.sendSimpleMessage(email,EMAIL_SUBJECT,EMAIL_TEXT+url);

            return new ResponseEntity<>(EMAIL_SEND,HttpStatus.OK);

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
            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(twoFactorSetRequest.getEmail()==null || twoFactorSetRequest.getValue()==null)
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(twoFactorSetRequest.getEmail()))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

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
            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(email==null)
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(Boolean.TRUE.equals(userRepository.existsByEmail(email)))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

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
            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(code==null)
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

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
            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(email==null)
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(Boolean.TRUE.equals(userRepository.existsByEmail(email)))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);


            //Generate recovery code
            String code = twoFactorService.generateRecovery()[authUtil.getRandomNumberUsingInts(0,twoFactorService.generateRecovery().length-1)];

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
     * Check Factor Recovery Code
     * */
    public ResponseEntity<Object> checkTwoFactoryRecovery(HttpServletRequest request, RecoveryCodeCheckRequest recoveryCodeCheckRequest){
        try {
            //Token validation
            if(Boolean.FALSE.equals(tokenValidation.validate(request)))
                return new ResponseEntity<>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

            //Request validation
            if(authUtil.anyNullObjectValue(recoveryCodeCheckRequest))
                return new ResponseEntity<>(EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);

            if(userRepository.existsByEmail(recoveryCodeCheckRequest.getEmail()))
                return new ResponseEntity<>(NOT_EXIST_EMAIL, HttpStatus.NOT_FOUND);

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
