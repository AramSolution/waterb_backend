package arami.adminWeb.artappm.service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 지원사업 신청 등록 요청 DTO
 */
@Data
public class ArtappmInsertRequest {

    /** 지원사업신청ID (PK). 미입력 시 서버에서 채번 */
    private String reqId;
    private String proId;
    private String proSeq;
    /** 신청회차 (PRO_SEQ와 동일 값으로 서버에서 세팅) */
    private String reqProSeq;
    private String reqEsntlId;
    /** 학생(자녀) 고유ID — 학부모 신청 시 REQ_ESNTL_ID는 신청자(부모), 본 필드는 학생 */
    @JsonProperty("cEsntlId")
    private String cEsntlId;
    /** 01:1인탐구형, 02:모둠 탐구형 */
    private String proType;
    /** 학부모ID */
    @JsonProperty("pEsntlId")
    private String pEsntlId;
    /** 세대주명 */
    private String headNm;
    @JsonProperty("pUserNm")
    private String pUserNm;
    private String mbtlnum;
    private String brthdy;
    /** 학부모 주민번호 */
    @JsonProperty("pIhidnum")
    private String pIhidnum;
    /** 학생 주민번호 */
    @JsonProperty("cIhidnum")
    private String cIhidnum;
    private String certYn;
    /** 보호자 인증 시 수신한 DI(개인식별코드) - ARTAPPM.CRTFC_DN_VALUE 저장 */
    private String crtfcDnValue;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private Integer schoolLvl;
    private Integer schoolNo;
    private String payBankCode;
    private String payBank;
    /** 예금주 */
    private String holderNm;
    private String reqPart;
    private String playPart;
    private String reqObj;
    private String reqPlay;
    private String reqPlan;
    /** 다자녀유무 */
    private String mchilYn;
    /** 다자녀명 */
    private String mchilNm;
    private String reqDesc;
    private String fileId;
    /** 첨부파일별 seq (프론트 지정, 예: 1=신청서, 2=동의서). 없으면 백엔드가 0,1,2 자동 부여 */
    private List<Integer> fileSeqs;
    private String resultGb;
    /** 신청일시 */
    private String reqDt;
    /** 상담일자 (ARTAPPM.WORK_DT) */
    private String workDt;
    /** 승인일시 */
    private String aprrDt;
    /** 변경일시 */
    private String chgDt;
    /** 중단일시 */
    private String stopDt;
    /** 사유 */
    private String reaDesc;
    private String sttusCode;
    private String UNIQ_ID;
    /** 사업구분(ARTPROM.PRO_GB). 02일 때만 첨부파일 FILE_DESC를 seq별(신청서 등)로 저장 */
    private String proGb;

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
}
