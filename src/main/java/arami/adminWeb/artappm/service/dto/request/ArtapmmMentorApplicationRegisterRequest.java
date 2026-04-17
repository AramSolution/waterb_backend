package arami.adminWeb.artappm.service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 관리자 멘토 신청 등록 (ARTAPMM) JSON 파트.
 * multipart 시 {@code @RequestPart("data")} 로 전달.
 */
@Data
public class ArtapmmMentorApplicationRegisterRequest {

    /** 지원사업 회차 (미입력 시 0) */
    private Integer proSeq;

    /** 멘토 회원 ESNTL_ID (필수) */
    @NotBlank
    private String reqEsntlId;

    @Size(max = 2048)
    private String reqPlay;
    @Size(max = 2048)
    private String reqDesc;

    /** 첨부파일 그룹 ID (업로드 시 컨트롤러에서 설정) */
    private String fileId;

    /** 첨부 seq (없으면 0,1,2…) */
    private List<Integer> fileSeqs;

    /** 선정여부 (Y/N/R). 미입력 시 N */
    private String resultGb;

    /** yyyy-MM-dd */
    private String reqDt;
    private String aprrDt;
    private String chgDt;
    private String stopDt;

    @Size(max = 2048)
    private String reaDesc;

    @Size(max = 512)
    private String collegeNm;
    private String leaveYn;
    @Size(max = 512)
    private String majorNm;
    private Integer schoolLvl;
    @Size(max = 64)
    private String studentId;
    @Size(max = 512)
    private String hschoolNm;

    private String reqReason;
    private String career;

    /** A/D 등. 미입력 시 A */
    private String sttusCode;

    /** 희망과목 (ARTAPMM.REQ_SUB, CHAR 4) */
    @Size(max = 4)
    private String reqSub;

    /** 멘토링 희망시간대 (ARTAPMM.JOIN_TIME, VARCHAR 64) */
    @Size(max = 64)
    private String joinTime;

    /** 이용동의 (ARTAPMM.AGREE1_YN) */
    private String agree1Yn;

    /** 제공동의 (ARTAPMM.AGREE2_YN) */
    private String agree2Yn;

    /** 파일 업로드 시 세션 UNIQ_ID (컨트롤러에서 설정) */
    private String uniqId;
}
