package hu.bb.travellingappauth.controller;

import hu.bb.travellingappauth.model.*;
import hu.bb.travellingappauth.service.AuthService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @ApiResponses(value = {
            @ApiResponse(description = "Password reset successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during reset password.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @PostMapping("/resetPw")
    public  ResponseEntity<String> resetPassword (@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request){
        return authService.resetPw(request, resetPasswordRequest);
    }

    //TODO: password forgot endpoint
    @ApiResponses(value = {
            @ApiResponse(description = "Password reset successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during reset password.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @GetMapping("/forgotPw")
    public  ResponseEntity<String> forgotPassword (@RequestParam("email") String email){
        return null;
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Two Factor Authorization changed successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during Two Factor Authorization changed.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @PostMapping("/mfa/set")
    public ResponseEntity<String> setTwoFactor(HttpServletRequest request, @RequestBody TwoFactorSetRequest twoFactorSetRequest){
        return authService.setTwoFactorAuth(request,twoFactorSetRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Get QR code successfully!",responseCode = "200",content = {@Content(mediaType = "application/octet-stream",schema = @Schema(implementation = MultipartFile.class))}),
            @ApiResponse(description = "Bad Request during getting QR code.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @GetMapping("/mfa/qr")
    public ResponseEntity<Object> getTwoFactorQr(HttpServletRequest request, @RequestParam("email") String email){
        return this.authService.getTwoFactorQrCode(request, email);
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Two Factor code checked successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(description = "Bad Request during two factor code checked.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
        }
    )
    @GetMapping("/mfa/check")
    public ResponseEntity<Object> checkTwoFactorCode(HttpServletRequest request, @RequestParam("code") String code){
        return this.authService.checkTwoFactorCode(request, code);
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Two Factor Recovery generated successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Bad Request during two factor recovery generation.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
    }
    )
    @GetMapping("/mfa/recovery")
    public ResponseEntity<String> getTwoFactorRecoveryCode(HttpServletRequest request, @RequestParam("email") String email){
        return this.authService.getTwoFactoryRecovery(request, email);
    }


    @ApiResponses(value = {
            @ApiResponse(description = "Two Factor Recovery checked successfully!",responseCode = "200",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(description = "Bad Request during two factor recovery checked.",responseCode = "400",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Unauthorized request.",responseCode = "401",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Email Not Found.",responseCode = "404",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))}),
            @ApiResponse(description = "Internal server error.",responseCode = "500",content = {@Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))})
    }
    )
    @PostMapping("/mfa/recovery")
    public ResponseEntity<Object> checkTwoFactorRecoveryCode(HttpServletRequest request, @RequestBody RecoveryCodeCheckRequest recoveryCodeCheckRequest){
        return this.authService.checkTwoFactoryRecovery(request, recoveryCodeCheckRequest);
    }

}
