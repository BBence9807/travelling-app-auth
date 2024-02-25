package hu.bb.travellingappauth.helper;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorUtil {

    @Bean
    public SecretGenerator secretGenerator(){
        return new DefaultSecretGenerator();
    }

    @Bean
    public TimeProvider timeProvider(){
        return new SystemTimeProvider();
    }

    @Bean
    public CodeGenerator codeGenerator(){
        return new DefaultCodeGenerator();
    }
}
