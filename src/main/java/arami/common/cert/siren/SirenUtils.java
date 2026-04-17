package arami.common.cert.siren;

import egovframework.com.cmm.service.EgovProperties;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SirenUtils {

    public String createCryptoTokenUrl = "https://sciapi.siren24.com:52099/authentication/api/v1.0/common/crypto/token";
    public String client_id            = EgovProperties.getProperty("bizsiren.clientId");

    // 암호화
    public String getEncReqData(String key, String iv, String reqData) throws Exception {
        String req_info = "";
        try {
            SecretKey secureKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted;
            encrypted = cipher.doFinal(reqData.trim().getBytes());
            req_info = Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println(String.format("(APICERT)(ERR) getEncReqData Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        }
        return req_info;
    }

    public byte[] hmac256(byte[] secretKey,byte[] message) throws Exception{
        byte[] hmac256 = null;
        try{
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sks = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(sks);
            hmac256 = mac.doFinal(message);
            return hmac256;
        } catch(Exception e){
            System.out.println(String.format("(APICERT)(ERR) hmac256 Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        }
    }

    // createSymmetricKey 생성
    public String createSymmetricKey(String req_dtim, String req_no, String token_val) throws Exception {
        String symmetricKey = "";
        String value = req_dtim.trim() + req_no.trim() + token_val.trim();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(value.getBytes());
            byte[] arrHashValue = md.digest();
            symmetricKey = Base64.getEncoder().encodeToString(arrHashValue);
        } catch (Exception e) {
            System.out.println(String.format("(APICERT)(ERR) createSymmetricKey Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        }
        return symmetricKey;
    }

    // crypto tokenAPI 호출
    public String callCreateCryptoTokenAPI(String req_date, String req_no) throws Exception {

        String authorization = "bearer " + EgovProperties.getProperty("bizsiren.accessToken");

        Map<String, String> requestPropertyMap = new HashMap<>();
        requestPropertyMap.put("Content-Type", "application/json;charset=utf-8");
        requestPropertyMap.put("Authorization", authorization);
        HttpURLConnection connection = getURLConnection(createCryptoTokenUrl, "POST", requestPropertyMap, true, true);

        JSONObject dataHeader = new JSONObject();
        dataHeader.put("lang_code", "kr");

        JSONObject dataBody = new JSONObject();
        dataBody.put("client_id", client_id);
        dataBody.put("req_date", req_date);
        dataBody.put("req_no", req_no);
        dataBody.put("enc_mode", "1");

        JSONObject msgMap = new JSONObject();
        msgMap.put("dataHeader", dataHeader);
        msgMap.put("dataBody", dataBody);

        String msg = msgMap.toJSONString();
        if(send(connection.getOutputStream(), msg)) return "";
        String receiveMsg = receive(connection.getInputStream());

        return receiveMsg;
    }

    public Boolean send(OutputStream outputStream, String sendMsg) throws Exception {
        Boolean isFail = true;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            bufferedWriter.write(sendMsg);
            bufferedWriter.flush();
            isFail = false;
        } catch (Exception e) {
            System.out.println(String.format("(APICERT)(ERR) send Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
        return isFail;
    }

    public String receive(InputStream inputStream) throws Exception {
        String receiveMsg = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            receiveMsg = stringBuilder.toString();
        } catch (Exception e) {
            System.out.println(String.format("(APICERT)(ERR) receive Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return receiveMsg;
    }

    public HttpURLConnection getURLConnection(String urlStr, String method, Map<String, String> requestPropertyMap, Boolean isNeedOutput, Boolean isHttps) throws Exception {
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method);

            connection.setDoInput(true);
            if (isNeedOutput) connection.setDoOutput(true);
            for (String key : requestPropertyMap.keySet()) {
                connection.setRequestProperty(key, requestPropertyMap.get(key));
            }
            return connection;
        } catch (Exception e) {
            System.out.println(String.format("(APICERT)(ERR) getURLConnection Exception : %s", e.getMessage()));
            e.printStackTrace();
            throw e;
        } finally {
        }
    }

    private HttpURLConnection getURLConnectionSSL(String urlStr, String method, Map<String,String> headers, boolean doOutput) throws Exception {
        URL url = new URL(urlStr);
        URLConnection uc = url.openConnection();
        if (uc instanceof HttpsURLConnection) {
            HttpsURLConnection h = (HttpsURLConnection) uc;
            h.setSSLSocketFactory(((SSLSocketFactory) SSLSocketFactory.getDefault()));
            h.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslSession) { return true; }
            });
            h.setRequestMethod(method);
            h.setDoInput(true);
            h.setDoOutput(doOutput);
            for (Map.Entry<String,String> e : headers.entrySet()) h.setRequestProperty(e.getKey(), e.getValue());
            return h;
        } else {
            HttpURLConnection h = (HttpURLConnection) uc;
            h.setRequestMethod(method);
            h.setDoInput(true);
            h.setDoOutput(doOutput);
            for (Map.Entry<String,String> e : headers.entrySet()) h.setRequestProperty(e.getKey(), e.getValue());
            return h;
        }
    }

    public String callServerToServerAPI(String crypto_token_id, String reqInfoEnc, String integrityValue) throws Exception {
        String url = "https://pcc.siren24.com/servlet/StoS";

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");

        JSONObject dataHeader = new JSONObject();
        dataHeader.put("CNTY_CD", "kr");
        dataHeader.put("TRAN_ID", "비즈사이렌 로그인 아이디");

        JSONObject dataBody = new JSONObject();
        dataBody.put("crypto_token_id", crypto_token_id);
        dataBody.put("reqInfo", reqInfoEnc);
        dataBody.put("integrity_value", integrityValue);

        JSONObject msg = new JSONObject();
        msg.put("dataHeader", dataHeader);
        msg.put("dataBody", dataBody);

        HttpURLConnection conn = getURLConnectionSSL(url, "POST", headers, true);
        send(conn.getOutputStream(), msg.toJSONString());
        return receive(conn.getInputStream());
    }

    public String getReqData(String id, String srvNo, String reqNum, String retUrl, String certDate, String certGb) {

        JSONObject msgMap = new JSONObject();
        msgMap.put("id", id);
        msgMap.put("srvNo", srvNo);
        msgMap.put("reqNum", reqNum);
        msgMap.put("retUrl", retUrl);
        msgMap.put("certDate", certDate);
        msgMap.put("certGb", certGb);

        String reqData = msgMap.toJSONString();
        System.out.println("reqDate=>"+reqData);
        return reqData;
    }

    public String getRtnData(String id, String reqcryptotokenid) {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("reqcryptotokenid", reqcryptotokenid);
        return o.toJSONString();
    }

    public String base64Sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(d);
    }

    public String makeReqNo() {
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }
}
