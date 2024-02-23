package hu.bb.travellingappauth;

import hu.bb.travellingappauth.service.TwoFactorService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Travelling Authentication Server", description = "Authentication Server for travelling app",version = "1.0"))
public class TravellingAppAuthApplication implements ApplicationRunner {

	@Autowired
	TwoFactorService twoFactorService;

	public static void main(String[] args) {
		SpringApplication.run(TravellingAppAuthApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		twoFactorService.generateQrSecret();
	}
}
