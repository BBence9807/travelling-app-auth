package hu.bb.travellingappauth.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    private SecretGenerator secretGenerator;
    private String secret;
    private TimeProvider timeProvider;
    private CodeGenerator codeGenerator;
    private CodeVerifier verifier;
    private RecoveryCodeGenerator recoveryCodes;
    private Integer RECOVERY_SIZE = 16;

    public void generateQrSecret(){
        this.secretGenerator = new DefaultSecretGenerator();
        this.secret = secretGenerator.generate();
        this.timeProvider = new SystemTimeProvider();
        this.codeGenerator = new DefaultCodeGenerator();
        this.verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        this.recoveryCodes = new RecoveryCodeGenerator();
    }

    public byte[] generateQrCode(String label, String issuer) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(label)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA256) // More on this below
                .digits(6)
                .period(30)
                .build();

        return new ZxingPngQrGenerator().generate(data);
    }

    public Boolean checkCode(String code){
        return verifier.isValidCode(secret, code);
    }

    public String[] generateRecovery(){
        return recoveryCodes.generateCodes(RECOVERY_SIZE);
    }
}
