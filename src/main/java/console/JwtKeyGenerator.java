package console;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {
        // HS256 알고리즘에 적합한 안전한 Secret Key 생성
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // 생성된 키를 문자열(Base64)로 인코딩하여 확인 (설정 파일 등에 저장할 때 사용)
        String secretString = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Generated Secret Key (Base64): " + secretString);
    }

}