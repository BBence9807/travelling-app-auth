package hu.bb.travellingappauth.controller;

import hu.bb.travellingappauth.model.User;
import hu.bb.travellingappauth.model.UserLoginRequest;
import hu.bb.travellingappauth.model.UserRegisterRequest;
import hu.bb.travellingappauth.service.AuthService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiResponses(value = {
            @ApiResponse(description = "Success sign in.",responseCode = "200",content = {@Content(mediaType = "text/plainn",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during sign up.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized access.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @PostMapping("/signIn")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginRequest){
        return authService.login(userLoginRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Success sign up.",responseCode = "201",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = User.class))}),
            @ApiResponse(description = "Email already exist.",responseCode = "302",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during sign up.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @PostMapping("/signUp")
    public  ResponseEntity<Object> register(@RequestBody UserRegisterRequest userRegisterRequest){
        return authService.register(userRegisterRequest);
    }

    //TODO: password reset endpoint

    //TODO: password forgot endpoint

}
