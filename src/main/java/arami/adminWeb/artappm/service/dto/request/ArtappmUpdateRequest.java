package arami.adminWeb.artappm.service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 지원사업 신청 수정 요청 DTO
 */
@Data
public class ArtappmUpdateRequest {

    /** 지원사업신청 PK (REQ_ID). 수정 조건은 REQ_ID만 사용 */
    private String reqId;
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    /** 학생(자녀) 고유ID */
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
    /** 보호자 인증 시 수신한 DI(개인식별코드) - ARTAPPM.CRTFC_DN_VALUE 저장, 값 있을 때만 업데이트 */
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
    /** 첨부파일별 seq (프론트 지정). 없으면 백엔드가 0,1,2 또는 append 시 nextSeq부터 자동 부여 */
    private List<Integer> fileSeqs;
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
    private String sttusCode;
    private String UNIQ_ID;
    /** 사업구분(ARTPROM.PRO_GB). 02일 때만 첨부파일 FILE_DESC를 seq별(신청서 등)로 저장 */
    private String proGb;
    /** 상담일자 (ARTAPPM.WORK_DT). proGb=03 진로진학 수정 시 사용 */
    private String workDt;

    // --- ARTAPPS (공부의 명수 PRO_GB=08) ---
    private String proGbn;
    @Size(max = 512)
    private String proGbnEtc;
    private String reqSub;
    private String joinCnt;
    private String befJoin;
    private String joinTime;
    private String joinTimeCon;
    @JsonProperty("sUnder")
    private String sUnder;
    @JsonProperty("sTarget")
    private String sTarget;
    @JsonProperty("sChar")
    private String sChar;
    @JsonProperty("sReason")
    private String sReason;
    @JsonProperty("sExpect")
    private String sExpect;
    @JsonProperty("sAppr")
    private String sAppr;
    @JsonProperty("sComm")
    private String sComm;
    private String agree1Yn;
    private String agree2Yn;
    private String agree3Yn;
}
