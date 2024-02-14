package hu.bb.travellingappauth.service;

import hu.bb.travellingappauth.helper.JwtUtil;
import hu.bb.travellingappauth.model.ResetPasswordRequest;
import hu.bb.travellingappauth.model.User;
import hu.bb.travellingappauth.model.UserLoginRequest;
import hu.bb.travellingappauth.model.UserRegisterRequest;
import hu.bb.travellingappauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final String EMPTY_DATA_ERROR = "Please provide all needed data! There are required!";
    private final String NOT_EXIST_EMAIL = "Email does not exist! Please provide valid email address!";
    private final String EXIST_EMAIL = "Email does already exist! Please provide valid email address!";
    private final String WRONG_PASSWORD = "Wrong password! Please provide valid password!";

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
    public ResponseEntity<String> resetPw(ResetPasswordRequest resetPasswordRequest){
        try {
            //Request body null validation
            if(resetPasswordRequest.getPassword()==null || resetPasswordRequest.getPasswordAgain() == null)
                return new ResponseEntity<>(this.EMPTY_DATA_ERROR, HttpStatus.BAD_REQUEST);



            return new ResponseEntity<>("",HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
