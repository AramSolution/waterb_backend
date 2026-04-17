package arami.adminWeb.artedum.service.dto.response;

import arami.common.files.service.FileDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 가맹학원 상세 조회 응답 (ARMUSER readonly + ARTEDUM + ARTEDUD 과목 목록)
 */
@Data
public class ArtedumDetailResponse {

    // ----- ARMUSER (학원 정보, readonly) -----
    private String esntlId;
    private String userId;
    private String userNm;
    private String emailAdres;
    private String usrTelno;
    private String mbtlnum;
    private String brthdy;
    private String ihidnum;
    private String sexdstnCode;
    private String zip;
    private String adresLot;
    private String adres;
    private String detailAdres;
    private String offmTelno;
    private String fxnum;
    private String schoolId;
    private String schoolGb;
    private String schoolNm;
    private Integer schoolLvl;
    private Integer schoolNo;
    private String payBankCode;
    private String payBankCodeNm;
    private String payBank;
    private String holderNm;
    private String mberSttus;
    private String mberSttusNm;
    private String sbscrbDe;
    private String secsnDe;
    private String lockAt;
    private String userSe;
    private String chgLastDt;
    private String groupId;
    private String orgnztId;
    private String indutyCode;
    private String bizrno;
    private String entrpsSeCode;
    private String cxfc;
    private String pstinstCode;
    private String ofcpsNm;
    private String emplNo;
    private String applcntNm;
    private String crtfcDnValue;
    private String crtfcCnValue;
    private String tokenId;
    private Integer lockCnt;
    private String lockLastPnttm;
    private String chgPwdLastPnttm;
    private String terms1Yn;
    private String terms2Yn;
    private String terms3Yn;
    private String terms4Yn;
    private String noticeYn;
    private String eventNotiYn;
    private String mailNotiYn;
    private String smsNotiYn;
    private String certYn;
    private String certDate;
    private String profile;
    private String profileGb;
    private String testYn;
    private String sessionKey;
    private String sessionLimit;
    private String approveDe;
    private String userPic;
    private String citizenYn;
    private String singleYn;
    private String minorYn;
    private String basicYn;
    private String poorYn;
    private String farmYn;
    private String attaFile;
    private String subjectDesc;
    private String profileDesc;
    private String biznoFile;

    // ----- ARTEDUM (가맹 신청 1건) -----
    private String eduGb;
    private String eduGbNm;
    private String eduFile;
    private String runSta;
    private String runStaNm;

    // ----- ARTEDUD 과목 목록 -----
    private List<ArtedudDetailItem> subjects = new ArrayList<>();

    /** EDU_FILE(첨부파일 그룹) 기준 파일 목록 (다운로드/삭제용 fileId, seq 포함) */
    @JsonProperty("files")
    private List<FileDTO> eduFileList = new ArrayList<>();
}
