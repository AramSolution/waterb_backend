package arami.common.cipher.crypto;

import arami.common.cipher.crypto.padding.PKCS5Padding;
import arami.common.cipher.crypto.symm.LEA;

import java.security.SecureRandom;
import java.util.Base64;

public class LEACrypto {

    private final static String seedKey = "l31*w@W30qS=HcuG";

    public static void main(String[] args) throws Exception {
        /*String message = "테스트 입니다";
        String key = "l31*w@W30qS=HcuG";
        String encodeData = encode(message);

        System.out.println(encodeData);
        System.out.println(decode(encodeData));*/

        System.out.println(generateRandomPassword(10));
    }

    public static String encode(String message) throws Exception {
        BlockCipherMode cipher = new LEA.CBC();

        // 암호화
        cipher.init(BlockCipher.Mode.ENCRYPT, seedKey.getBytes(), reverseString(seedKey).getBytes());
        cipher.setPadding(new PKCS5Padding(16));
        //byte[] ct1 = cipher.update(messageBytes);
        byte[] encodeMsg = cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encodeMsg);
    }

    public static String decode(String message) throws Exception {
        BlockCipherMode cipher = new LEA.CBC();

        // 복호화
        cipher.init(BlockCipher.Mode.DECRYPT, seedKey.getBytes(), reverseString(seedKey).getBytes());
        cipher.setPadding(new PKCS5Padding(16));
        //byte[] ct1 = cipher.update(messageBytes);
        byte[] decodeMsg = cipher.doFinal(Base64.getDecoder().decode(message));

        return new String(decodeMsg);
    }

    public static String generateRandomString(int length) {
        String charStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*/+-=";
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charStr.length());
            sb.append(charStr.charAt(index));
        }
        return sb.toString();
    }

    public static String reverseString(String input) {
        char[] charArray = input.toCharArray();
        StringBuilder reversedString = new StringBuilder();
        for (int i = charArray.length - 1; i >= 0; i--) {
            reversedString.append(charArray[i]);
        }
        return reversedString.toString();
    }

    public static String generateRandomPassword(int length) {
        String charStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String charNum = "0123456789";

        if (length < 8) {
            throw new IllegalArgumentException("패스워드 길이는 최소 2 이상이어야 합니다.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 숫자에서 1개 랜덤 선택 (숫자를 반드시 포함시킴)
        int numberIndex = random.nextInt(charNum.length());
        char randomNumber = charNum.charAt(numberIndex);
        password.append(randomNumber);

        // 나머지 자리를 랜덤하게 채우기
        for (int i = 1; i < length; i++) {
            int randomIndex = random.nextInt(charStr.length());
            password.append(charStr.charAt(randomIndex));
        }

        // 패스워드의 문자 순서를 랜덤하게 섞기
        return shuffleString(password.toString(), random);
    }

    private static String shuffleString(String input, SecureRandom random) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int randomIndex = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[randomIndex];
            chars[randomIndex] = temp;
        }
        return new String(chars);
    }

}