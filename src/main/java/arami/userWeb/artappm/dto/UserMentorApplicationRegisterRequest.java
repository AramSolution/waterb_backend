package arami.userWeb.artappm.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 사용자웹 멘토 신청 API의 JSON 파트. 저장 시 {@code edream.artapmm}(ARTAPMM)에 매핑된다.
 * <p>[WATERB_MIGRATION_C] {@code edream}은 MySQL 스키마명이다. DB를 {@code waterb} 등으로 옮기면 JPA/MyBatis·권한·문서와 함께 점검.
 * <p>
 * {@code REQ_ESNTL_ID}(신청자)는 ARTAPMM 컬럼이나 본 DTO에 포함하지 않으며, 서버에서 로그인 사용자로만 설정한다.
 * {@code proGb}는 ARTPROM.PRO_GB 검증용(08·09)이며 ARTAPMM 테이블 컬럼은 아니다.
 * {@code CHG_USER_ID}·{@code CRT_DATE}·{@code CHG_DATE}는 서버/DB에서 처리한다.
 */
@Data
public class UserMentorApplicationRegisterRequest {

    /**
     * 사업 구분 검증용. ARTPROM.PRO_GB와 동일해야 함 (08=온라인 튜터링, 09=인생등대). ARTAPMM 컬럼 아님.
     */
    @NotBlank
    @Pattern(regexp = "08|09")
    private String proGb;

    /** {@code PRO_SEQ} — 회차 (기본 0). */
    private Integer proSeq;

    /** {@code REQ_PLAY} varchar(2048) — 신청내용. */
    @Size(max = 2048)
    private String reqPlay;

    /** {@code REQ_DESC} varchar(2048) — 비고. */
    @Size(max = 2048)
    private String reqDesc;

    /**
     * {@code FILE_ID} char(20) — 첨부파일 그룹 ID. 업로드 시 컨트롤러에서 설정할 수 있음.
     */
    private String fileId;

    /**
     * 첨부 멀티파트 시 파일별 SEQ 지정용. ARTAPMM 컬럼 아님(ARTFILE 등과 연계).
     */
    private List<Integer> fileSeqs;

    /** {@code RESULT_GB} char(2) — 선정여부. */
    private String resultGb;

    /** {@code REQ_DT} date — 신청일시 (API는 문자열, 예: yyyy-MM-dd). */
    private String reqDt;

    /** {@code APRR_DT} date — 승인일시. */
    private String aprrDt;

    /** {@code CHG_DT} date — 변경일시. */
    private String chgDt;

    /** {@code STOP_DT} date — 중단일시. */
    private String stopDt;

    /** {@code REA_DESC} varchar(2048) — 사유. */
    @Size(max = 2048)
    private String reaDesc;

    /** {@code COLLEGE_NM} varchar(512) — 대학교명. */
    @Size(max = 512)
    private String collegeNm;

    /** {@code LEAVE_YN} char(1) — 휴학유무 (기본 N). */
    private String leaveYn;

    /** {@code MAJOR_NM} varchar(512) — 학과. */
    @Size(max = 512)
    private String majorNm;

    /** {@code SCHOOL_LVL} int — 학년. */
    private Integer schoolLvl;

    /** {@code STUDENT_ID} varchar(64) — 학번. */
    @Size(max = 64)
    private String studentId;

    /** {@code HSCHOOL_NM} varchar(512) — 고등학교. */
    @Size(max = 512)
    private String hschoolNm;

    /** {@code REQ_REASON} text — 신청동기. */
    private String reqReason;

    /** {@code CAREER} text — 경력정보. */
    private String career;

    /** {@code STTUS_CODE} char(1) — 상태 (기본 A). */
    private String sttusCode;

    /** {@code REQ_SUB} char(4) — 희망과목. */
    @Size(max = 4)
    private String reqSub;

    /** {@code JOIN_TIME} varchar(64) — 멘토링 희망시간대. */
    @Size(max = 64)
    private String joinTime;

    /** {@code AGREE1_YN} char(1) — 이용동의. */
    private String agree1Yn;

    /** {@code AGREE2_YN} char(1) — 제공동의. */
    private String agree2Yn;

    /**
     * 파일 업로드 시 세션/사용자 UNIQ_ID(컨트롤러에서 설정). ARTAPMM 컬럼 아님.
     */
    private String uniqId;
}
