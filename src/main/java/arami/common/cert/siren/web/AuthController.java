package arami.common.cert.siren.web;

import arami.common.CommonService;
import arami.common.auth.service.MemberLoginDAO;
import arami.common.auth.service.dto.CrtfcDnUserSeParam;
import arami.common.cert.comm.MobileCertDTO;
import arami.common.cert.comm.service.MobileCertService;
import arami.shared.armuser.service.ArmuserService;
import arami.shared.armuser.dto.response.ArmuserCrtfcDnValueCheckResponse;
import arami.common.cert.siren.SirenUtils;
import arami.common.cert.siren.service.AuthTokenVO;
import arami.common.cert.siren.service.CertResultVO;
import arami.common.cert.siren.service.RequestCertVO;
import arami.common.cert.siren.service.TokenResponVO;
import egovframework.com.jwt.EgovJwtTokenUtil;
import egovframework.com.cmm.service.EgovProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cert/siren")
public class AuthController extends CommonService {

    private SirenUtils sirenUtils = new SirenUtils();

    @Resource(name="MobileCertService")
    private MobileCertService mobileCertService;

    @Resource(name = "armuserService")
    private ArmuserService armuserService;

    @Resource(name = "memberLoginDAO")
    private MemberLoginDAO memberLoginDAO;

    @Autowired
    private EgovJwtTokenUtil jwtTokenUtil;


    @PostMapping("/tokenAuth")
    public ResponseEntity<AuthTokenVO> tokenAuth(HttpServletRequest request, ModelMap model, RequestCertVO requestCertVO) throws Exception {
        this.setCommon(request, model);

        //날짜 생성
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String day = sdf.format(today.getTime());
        Date currentDate = new Date();


        Random ran = new Random();
        //랜덤 문자 길이
        int numLength = 6;
        String randomStr = "";

        for (int i = 0; i < numLength; i++) {
            //0 ~ 9 랜덤 숫자 생성
            randomStr += ran.nextInt(10);
        }

        //reqNum은 최대 40byte 까지 사용 가능
        String reqNum   = day + randomStr; // 고정값으로 사용가능
        String certDate = day;

        AuthTokenVO authTokenVO = AuthTokenVO.builder()
                .srvNo(requestCertVO.getSrvNo())
                .reqNum(reqNum)
                .retUrl(requestCertVO.getRetUrl())
                .certDate(certDate)
                .build();

        log.info("본인인증 요청: {}", authTokenVO);

        return ResponseEntity.ok(authTokenVO);
    }

    @PostMapping("/createToken")
    public ResponseEntity<TokenResponVO> createToken(HttpServletRequest request, ModelMap model, AuthTokenVO authTokenVO) throws Exception {
        this.setCommon(request, model);

        TokenResponVO tokenResponVO = null;

        try{
            Date currentDate = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            String req_date = fmt.format(currentDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            String cryptoToken = sirenUtils.callCreateCryptoTokenAPI(req_date, authTokenVO.getReqNum());

            JSONParser parser = new JSONParser();
            JSONObject cryptoTokenJson = (JSONObject) parser.parse(cryptoToken);
            JSONObject dataBody = (JSONObject) cryptoTokenJson.get("dataBody");

            String crypto_token_id = (String) dataBody.get("crypto_token_id");
            String token_val = (String) dataBody.get("crypto_token");
            String reqInfo = sirenUtils.getReqData(authTokenVO.getId(), authTokenVO.getSrvNo(), authTokenVO.getReqNum(), authTokenVO.getRetUrl(), authTokenVO.getCertDate(), authTokenVO.getCertGb());

            String symmetricKey = sirenUtils.createSymmetricKey(req_date, authTokenVO.getReqNum(), token_val);
            String key = symmetricKey.substring(0, 16);
            String iv = symmetricKey.substring(symmetricKey.length() - 16, symmetricKey.length());// 데이터 암호화할  lnitail Vector

            reqInfo = sirenUtils.getEncReqData(key, iv, reqInfo);

            String hmac_key = symmetricKey.substring(0, 32); // 암복호화 위변조 체크용req
            byte[] hmacSha256 = sirenUtils.hmac256(hmac_key.getBytes(), reqInfo.getBytes());
            String integrity_value = Base64.getEncoder().encodeToString(hmacSha256);

            tokenResponVO = TokenResponVO.builder()
                    .cryptoTokenId(crypto_token_id)
                    .integrityValue(integrity_value)
                    .reqInfo(reqInfo)
                    .verSion("3")
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok(tokenResponVO);
    }

    @PostMapping("/resultData")
    public ResponseEntity<CertResultVO> resultData(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        // log.info("TEST RESULT API ================================================");
        // System.out.println("TEST RESULT API ================================================");
        // System.out.println("TEST RESULT API model: " + model.toString());
        CertResultVO certResultVO = new CertResultVO();

        // ===== 1) 표준창 리턴 파라미터/세션 값 =====
        String reqcryptotokenid = model.get("crypto_token_id").toString() != null ? model.get("crypto_token_id").toString().trim() : null;
        String id = EgovProperties.getProperty("bizsiren.id");

        // ===== 2) StoS용 암호화 토큰 발급 (createCryptoToken) =====
        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String req_date = fmt.format(now);
        String reqNo = sirenUtils.makeReqNo();
        String tokenResp = null;
        try {
            tokenResp = sirenUtils.callCreateCryptoTokenAPI(req_date, reqNo);
            //System.out.println("[DEBUG] TokenResp: " + tokenResp);
        } catch (Exception e) {
            //System.out.println("<p style='color:red'>토큰 API 호출 실패: " + e.getMessage() + "</p>");
            certResultVO.setRspCd("500");
            certResultVO.setResMsg("토큰 API 호출 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(certResultVO);
        }

        JSONParser parser = new JSONParser();
        JSONObject tokenJson = (JSONObject) parser.parse(tokenResp);
        JSONObject tokenBody = (tokenJson != null) ? (JSONObject) tokenJson.get("dataBody") : null;
        if (tokenBody == null) {
            //System.out.println("<p style='color:red'>토큰 API 응답 파싱 실패</p>");
            certResultVO.setRspCd("500");
            certResultVO.setResMsg("토큰 API 응답 파싱 실패");
            return ResponseEntity.status(500).body(certResultVO);
        }

        String crypto_token_id = (String) tokenBody.get("crypto_token_id");
        String crypto_token    = (String) tokenBody.get("crypto_token");

        // System.out.println("TEST RESULT API ============================ crypto_token_id / crypto_token : " + crypto_token_id + " / " + crypto_token);

        // ===== 3) StoS 요청용 대칭키 파생 & reqInfo 암호화 =====
        String symmetricKey = sirenUtils.createSymmetricKey(req_date, reqNo, crypto_token); // Base64(SHA-256)
        String key = symmetricKey.substring(0, 16);
        String iv  = symmetricKey.substring(symmetricKey.length() - 16);

        String reqInfoPlain = sirenUtils.getRtnData(id, reqcryptotokenid);
        String reqInfoEnc   = sirenUtils.getEncReqData(key, iv, reqInfoPlain);

        String integrity_value = sirenUtils.base64Sha256(req_date + reqNo + crypto_token);

        // ===== 4) 인증결과API 호출 → RET_INFO 수신 =====
        String stosResp = null;
        try {
            stosResp = sirenUtils.callServerToServerAPI(crypto_token_id, reqInfoEnc, integrity_value);
            // System.out.println("<b>StoS Raw Response</b><br><pre>" + stosResp + "</pre><br>");
        } catch (Exception e) {
            //System.out.println("<p style='color:red'>StoS 호출 실패: " + e.getMessage() + "</p>");
            certResultVO.setRspCd("500");
            certResultVO.setResMsg("StoS 호출 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(certResultVO);
        }

        JSONObject stosJson = (JSONObject) parser.parse(stosResp);
        JSONObject stosBody = (stosJson != null) ? (JSONObject) stosJson.get("dataBody") : null;
        if (stosBody == null) {
            //System.out.println("<p style='color:red'>StoS 응답 파싱 실패</p>");
            certResultVO.setRspCd("500");
            certResultVO.setResMsg("StoS 응답 파싱 실패");
            return ResponseEntity.status(500).body(certResultVO);
        }

        String rsp_cd   = (String) stosBody.get("rsp_cd");
        String ret_info = (String) stosBody.get("RET_INFO");
        // System.out.println("<b>StoS 결과</b><br>rsp_cd=" + rsp_cd + "<br>RET_INFO=" + ret_info + "<br><br>");

        if (!"P000".equals(rsp_cd)) {
            //System.out.println("<p style='color:red'>StoS 오류 코드: " + rsp_cd + "</p>");
            certResultVO.setRspCd("500");
            certResultVO.setResMsg("StoS 오류 코드: " + rsp_cd);
            return ResponseEntity.status(500).body(certResultVO);
        }

        // ===== 5) RET_INFO 복호화 (표준창 때의 reqkey/reqiv 사용) =====
        try {
            SecretKeySpec reqKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, reqKeySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

            byte[] enc = Base64.getDecoder().decode(ret_info);
            String resData = new String(cipher.doFinal(enc), StandardCharsets.UTF_8);

            JSONObject finalJson = (JSONObject) parser.parse(resData);
            System.out.println("<h3>복호화된 최종 결과</h3>");
            System.out.println(finalJson.toJSONString());


            // 이력데이터 생성
            MobileCertDTO mobileCertDTO = MobileCertDTO.builder()
                    .reqSeq(finalJson.get("id").toString())
                    .authType(finalJson.get("certGb").toString())
                    .cipherTime(finalJson.get("certdate").toString())
                    .resSeq(finalJson.get("reqNum").toString())
                    .name(finalJson.get("userName").toString())
                    .birthDate(finalJson.get("birYMD").toString())
                    .gender(finalJson.get("gender").toString())
                    .nationaInfo(finalJson.get("fgnGbn").toString())
                    .dupInfo(finalJson.get("di").toString())
                    .connInfo(finalJson.get("ci").toString())
                    .mobileCo(finalJson.get("Commid").toString())
                    .mobileNo(finalJson.get("celNo").toString())
                    .errCode(finalJson.get("result").toString())
                    .errMsg(finalJson.get("addVar").toString())
                    .build();

            System.out.println("mobileCertDTO==>" + mobileCertDTO.toString());

            mobileCertService.insertMobileCert(mobileCertDTO);

            certResultVO.setUserName(finalJson.get("userName").toString());
            certResultVO.setCelNo(finalJson.get("celNo").toString());
            certResultVO.setBirYMD(finalJson.get("birYMD").toString());
            certResultVO.setDi(finalJson.get("di").toString());

            String di = finalJson.get("di").toString();

            // retUrl certFlow=accountRecovery — 가입된 회원만 DI+USER_SE 일치 시 recoveryToken 발급 (아이디/비밀번호 찾기)
            if (isCertFlowAccountRecovery(model)) {
                String userSe = getCertUserSe(model);
                if (!StringUtils.hasText(userSe)) {
                    certResultVO.setRspCd("500");
                    certResultVO.setResMsg("회원 유형 정보가 없습니다. 인증을 다시 진행해 주세요.");
                    return ResponseEntity.status(500).body(certResultVO);
                }
                CrtfcDnUserSeParam param = new CrtfcDnUserSeParam();
                param.setUserSe(userSe.trim());
                param.setCrtfcDnValue(di.trim());
                String foundUserId = memberLoginDAO.selectUserIdByCrtfcDnAndUserSe(param);
                if (foundUserId == null || foundUserId.isBlank()) {
                    certResultVO.setRspCd("500");
                    certResultVO.setResMsg("일치하는 회원 정보가 없습니다.");
                    return ResponseEntity.status(500).body(certResultVO);
                }
                certResultVO.setRecoveryToken(jwtTokenUtil.generateAccountRecoveryToken(di.trim(), userSe.trim()));
                certResultVO.setRspCd("200");
                certResultVO.setResMsg("인증 되었습니다.");
                return ResponseEntity.ok(certResultVO);
            }

            // retUrl에 certFlow=apply(지원사업 신청 등): 로그인 후 본인확인만 수행 — DI가 이미 ARMUSER에 있어도 중복가입으로 거절하지 않음(프론트에서 CRTFC_DN_VALUE와 비교)
            if (!isCertFlowApply(model)) {
                ArmuserCrtfcDnValueCheckResponse armuserCrtfcDnValueCheckResponse = armuserService.selectCrtfcDnValueCheck(di);
                if (armuserCrtfcDnValueCheckResponse.getExist() == 1) {
                    certResultVO.setRspCd("500");
                    certResultVO.setResMsg("입력하신 정보로 이미 가입된 계정이 존재합니다.");
                    return ResponseEntity.status(500).body(certResultVO);
                }
            }

            certResultVO.setRspCd("200");
            certResultVO.setResMsg("인증 되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RET_INFO 복호화 실패: " + e.getMessage());
        }

        return ResponseEntity.ok(certResultVO);
    }

    /** tokenAuth retUrl 쿼리 certFlow=apply — 지원사업 신청 본인인증(회원가입·MYPAGE용 DI 중복 검사 유지) */
    private static boolean isCertFlowApply(ModelMap model) {
        Object v = model != null ? model.get("certFlow") : null;
        if (v == null) {
            return false;
        }
        String s = v.toString().trim();
        return StringUtils.hasText(s) && "apply".equalsIgnoreCase(s);
    }

    /** tokenAuth retUrl certFlow=accountRecovery — 아이디/비밀번호 찾기(가입 회원만 recoveryToken 발급) */
    private static boolean isCertFlowAccountRecovery(ModelMap model) {
        Object v = model != null ? model.get("certFlow") : null;
        if (v == null) {
            return false;
        }
        String s = v.toString().trim();
        return StringUtils.hasText(s) && "accountRecovery".equalsIgnoreCase(s);
    }

    private static String getCertUserSe(ModelMap model) {
        Object v = model != null ? model.get("userSe") : null;
        return v != null ? v.toString().trim() : "";
    }

}