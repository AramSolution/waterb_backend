package arami.common.cert.siren.service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResultDataDTO {

    private String id;         // 회원사 아이디
    private String result;     // 인증성공 여부(Y:성공, N:실패)
    private String reqNum;     // 요청번호(전달 받은 값 그대로 return)
    private String certDate;   // 요청일시(YYYYMMDDHI24MISS)
    private String userName;   // 이름
    private String birYMD;     // 생년월일(YYYYMMDD)
    private String gender;     // 성별(M:남자, F:여자)
    private String fgnGbn;     // 내외국인정보(1:내국인, 2:외국인)
    private String di;         // 개인식별코드(DI)
    private String ciVersion;  // 개인식별코드 CI버전
    private String ci;         // 개인식별코드(ciVersion이 1인경우)
    private String ci2;        // 개인식별코드(ciVersion이 2인경우)
    private String certGb;     // 인증수단(H:휴대폰)
    private String celNo;      // 휴대폰번호
    private String Commid;     // 이통사구분
    private String addVar;     // 추가파라미터

}
