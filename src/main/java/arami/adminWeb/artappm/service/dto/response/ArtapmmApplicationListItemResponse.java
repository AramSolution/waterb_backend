package arami.adminWeb.artappm.service.dto.response;

import java.util.ArrayList;
import java.util.List;

import arami.common.files.service.FileDTO;
import lombok.Data;

/**
 * 멘토 신청(ARTAPMM) 목록 1건 — ARTAPMM 전 컬럼 + ARMUSER 일부.
 */
@Data
public class ArtapmmApplicationListItemResponse {

    private String rnum;

    private String reqId;
    private String proId;
    private Integer proSeq;
    /** ARTPROM 사업명 */
    private String proNm;
    private String reqEsntlId;
    private String reqPlay;
    private String reqDesc;
    private String fileId;
    private String resultGb;
    private String reqDt;
    private String aprrDt;
    private String chgDt;
    private String stopDt;
    private String reaDesc;
    private String collegeNm;
    private String leaveYn;
    private String majorNm;
    private Integer schoolLvl;
    private String studentId;
    private String hschoolNm;
    private String reqReason;
    private String career;
    private String reqSub;
    private String joinTime;
    private String agree1Yn;
    private String agree2Yn;
    private String sttusCode;
    private String chgUserId;
    private String crtDate;
    private String chgDate;

    private String userSe;
    private String userId;
    private String userNm;
    private String usrTelno;
    private String mbtlnum;
    private String emailAdres;
    private String brthdy;
    private String mberSttus;

    /** ARMUSER 주소 조합 — REQ_ID 단건 상세(selectArtapmmApplicationByReqId)에서 채움 */
    private String fullAdres;
    /** ARMUSER.PROFILE_DESC — 상세 조회에서만 채움 */
    private String profileDesc;

    /**
     * 첨부파일 메타 목록 — GET .../mentor-applications/{reqId} 에서만 FILE_ID 기준 조회 후 설정.
     */
    private List<FileDTO> files = new ArrayList<>();
}
