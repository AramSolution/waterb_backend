package arami.adminWeb.artapps.service.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 공부의명수 수정 요청 DTO
 * multipart/form-data 시 "data" 파트에 JSON으로 전달. 날짜(recFromDd, recToDd)는 YYYYMMDD 문자열.
 */
@Data
public class ArtappsUpdateRequest {

    @NotBlank(message = "지원사업신청ID(reqId)는 필수입니다.")
    private String reqId;
    private String reqProSeq;
    @NotBlank(message = "공부의명수 ID(proId)는 필수입니다.")
    private String proId;
    private String proSeq;
    private String reqEsntlId;
    @JsonProperty("cEsntlId")
    private String cEsntlId;
    @JsonProperty("pEsntlId")
    private String pEsntlId;
    private String headNm;
    @JsonProperty("pUserNm")
    private String pUserNm;
    private String mbtlnum;
    private String brthdy;
    @JsonProperty("pIhidnum")
    private String pIhidnum;
    @JsonProperty("cIhidnum")
    private String cIhidnum;
    private String certYn;
    private String crtfcDnValue;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private Integer schoolLvl;
    private Integer schoolNo;
    private String payBankCode;
    private String payBank;
    private String holderNm;
    private String reqPart;
    private String playPart;
    private String reqObj;
    private String reqPlay;
    private String reqPlan;
    private String mchilYn;
    private String mchilNm;
    private String reqDesc;
    private String resultGb;
    private String reqDt;
    private String aprrDt;
    private String chgDt;
    private String stopDt;
    private String reaDesc;
    private String workDt;

    private String proGb;
    private String proType;
    /** 신청구분(학생|학부모|학원|멘토|학교 순서, 예: Y|N|N|N|N) */
    @Size(max = 12)
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
    private String proFileId;
    private String fileId;
    /** 진행상태(01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). UPDATE RUN_STA */
    private String runSta;
    private String sttusCode;
    /** 최종변경자(로그인 사용자 uniqId). SQL CHG_USER_ID = #{UNIQ_ID} */
    private String UNIQ_ID;

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

    /** 첨부 추가 시 파일별 SEQ (없으면 자동). multipart 순서와 1:1 */
    private List<Integer> fileSeqs;
}
