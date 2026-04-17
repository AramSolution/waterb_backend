package arami.common.cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import egovframework.com.cmm.service.EgovProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Base64;

public class LeaEncryptionUtil {

    static {
        // Bouncy Castle 프로바이더 등록 (애플리케이션 시작 시 한 번만 실행되면 됨)
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static final String ALGORITHM = "LEA/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    // 실무에서는 반드시 환경변수나 Vault에서 로드하세요. (32바이트 = 256비트)
    private static final byte[] KEY = EgovProperties.getProperty("Globals.crypto.key").getBytes();

    public static String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv); // 매번 랜덤 IV 생성

        Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, "LEA");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        // IV와 암호문을 결합하여 Base64 인코딩
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String cipherText) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(cipherText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decoded, 0, iv, 0, iv.length);

        byte[] encrypted = new byte[decoded.length - IV_LENGTH_BYTE];
        System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, "LEA");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        return new String(cipher.doFinal(encrypted));
    }
}