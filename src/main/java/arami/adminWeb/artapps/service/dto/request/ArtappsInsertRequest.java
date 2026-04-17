package arami.adminWeb.artapps.service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 공부의명수 신청 요청 DTO
 * multipart/form-data 시 "data" 파트에 JSON으로 전달. 날짜(recFromDd, recToDd)는 YYYYMMDD 문자열.
 */
@Data
public class ArtappsInsertRequest {

    /** 지원사업신청ID (REQ_ID) */
    private String reqId;
    /** 신청순번 (REQ_PRO_SEQ) */
    private String reqProSeq;
    /** 사업 ID */
    private String proId;
    /** 사업회차 (PRO_SEQ) */
    private String proSeq;
    /** 신청자(학생) ESNTL_ID */
    private String reqEsntlId;
    /** 학생 ESNTL_ID (학부모 신청 시) */
    @JsonProperty("cEsntlId")
    private String cEsntlId;
    /** 학부모 ESNTL_ID */
    @JsonProperty("pEsntlId")
    private String pEsntlId;
    /** 세대주명 */
    private String headNm;
    /** 보호자명 */
    @JsonProperty("pUserNm")
    private String pUserNm;
    /** 보호자 연락처 */
    private String mbtlnum;
    /** 보호자 생년월일 */
    private String brthdy;
    /** 학부모 주민번호 */
    @JsonProperty("pIhidnum")
    private String pIhidnum;
    /** 학생 주민번호 */
    @JsonProperty("cIhidnum")
    private String cIhidnum;
    /** 인증여부 */
    private String certYn;
    /** 인증값 */
    private String crtfcDnValue;
    /** 학교 ID */
    private String schoolId;
    /** 학교구분 */
    private String schoolGb;
    /** 학교명 */
    private String schoolNm;
    /** 학년 */
    private Integer schoolLvl;
    /** 반 */
    private Integer schoolNo;
    /** 은행코드 */
    private String payBankCode;
    /** 은행명 */
    private String payBank;
    /** 예금주 */
    private String holderNm;
    /** 신청분야 */
    private String reqPart;
    /** 활동범위 */
    private String playPart;
    /** 목적 */
    private String reqObj;
    /** 활동내용 */
    private String reqPlay;
    /** 예산 사용계획 */
    private String reqPlan;
    /** 다자녀유무 */
    private String mchilYn;
    /** 다자녀명 */
    private String mchilNm;
    /** 기타 */
    private String reqDesc;
    /** 결과구분 */
    private String resultGb;
    /** 신청일시 */
    private String reqDt;
    /** 승인일시 */
    private String aprrDt;
    /** 변경일시 */
    private String chgDt;
    /** 중단일시 */
    private String stopDt;
    /** 사유 */
    private String reaDesc;
    /** 상담일자 */
    private String workDt;

    private String proGb;
    private String proType;
    /** 신청구분(학생|학부모|학원|멘토|학교 순서, 예: Y|N|N|N|N) */
    @Size(max = 15)
    private String reqGb;
    @NotBlank(message = "사업명을 입력하세요")
    @Size(max = 200)
    private String proNm;
    @Size(max = 100)
    private String proTargetNm;
    /** 기타내용 */
    private String etcNm;
    /** 희망사업 */
    private String eduGb;
    /** 기초생활수급자여부 (Y/N) */
    private String basicYn;
    /** 차상위계층여부 (Y/N) */
    private String poorYn;
    /** 한부모가족여부 (Y/N) */
    private String singleYn;
    private String proTarget;
    @Size(max = 8)
    private String recFromDd;
    @Size(max = 8)
    private String recToDd;
    private Integer recCnt;
    private String proSum;
    private String proDesc;
    /** 홍보파일 그룹 ID (파일 업로드 후 서버 설정) */
    private String proFileId;
    /** 첨부파일 그룹 ID (파일 업로드 후 서버 설정) */
    private String fileId;
    /** 진행상태(01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). INSERT RUN_STA */
    private String runSta;
    private String sttusCode;
    /** 최종변경자(로그인 사용자 uniqId). 없으면 세션에서 설정 */
    private String UNIQ_ID;

    /** 공부의명수 신청ID(REQ_ID) */
    private String reqAppsId;
    /** 구분(01=기초생활수급, 02=법적차상위계층, 03=다자녀가정, 04=다문화가정, 05=일반, 06=기타) */
    private String proGbn;
    /** 구분 기타내용 */
    @Size(max = 512)
    private String proGbnEtc;
    /** 신청과목 - EDR006(중:01, 고::02) */
    private String reqSub;
    /** 희망참여횟수 - EDR006(08) */   
    private String joinCnt;
    /** 기존참여횟수 - EDR006(09) - 여러건 */
    private String befJoin;
    /** 멘토링희망시간대 - EDR006(중:03, 고:04)-여러건 */
    private String joinTime;
    /** 멘토링희망시간대 확정 - EDR006(중:03, 고:04) */
    private String joinTimeCon;
    /** 학업이해도 */
    @JsonProperty("sUnder")
    private String sUnder;
    /** 학업목표 */
    @JsonProperty("sTarget")
    private String sTarget;
    /** 학업특성 */
    @JsonProperty("sChar")
    private String sChar;
    /** 참여이유 */
    @JsonProperty("sReason")
    private String sReason;
    /** 기대효과 */
    @JsonProperty("sExpect")
    private String sExpect;
    /** 학부모평가 */
    @JsonProperty("sAppr")
    private String sAppr;
    /** 의견 */
    @JsonProperty("sComm")
    private String sComm;
    /** 의용동의여부(Y: 동의, N: 미동의) */
    private String agree1Yn;
    /** 제공동의여부(Y: 동의, N: 미동의) */
    private String agree2Yn;
    /** 주의사항동의여부(Y: 동의, N: 미동의) */
    private String agree3Yn;

    /** 첨부파일별 ARTFILE SEQ (없으면 0,1,2… 자동). multipart 순서와 1:1 */
    private List<Integer> fileSeqs;
}
