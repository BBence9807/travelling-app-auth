package hu.bb.travellingappauth.service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    @Autowired
    private SecretGenerator secretGenerator;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private CodeGenerator codeGenerator;

    private String secret;
    private CodeVerifier verifier;
    private RecoveryCodeGenerator recoveryCodes;
    private static final Integer RECOVERY_SIZE = 16;

    public void generateQrSecret(){
        this.secret = secretGenerator.generate();
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
