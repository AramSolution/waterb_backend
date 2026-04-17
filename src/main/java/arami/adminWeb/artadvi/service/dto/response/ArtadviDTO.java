package arami.adminWeb.artadvi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

import arami.common.files.service.FileDTO;

/**
 * 상담관리(ARTADVI) DTO
 */
@Data
public class ArtadviDTO {
    private String proId;
    private String proSeq;
    /** 상담관리 PK (조회 키) */
    private String reqId;
    private String reqEsntlId;
    @JsonProperty("advEsntlId")
    private String advEsntlId;
    private String advEsntlNm;
    /** 멘토 연락처 (ARMUSER.MBTLNUM 마스킹) */
    private String mbtlnum;
    /** 멘토 프로필 (ARMUSER.PROFILE_DESC) */
    private String profileDesc;
    /** 상담일자 (yyyy-MM-dd, 타임존 오차 방지용 문자열) */
    private String advDt;
    private String advFrom;
    private String advTo;
    private String advSpace;
    private String advDesc;
    private String fileId;
    /** ARTFILE에서 FILE_ID로 조회한 첨부파일 목록 */
    private List<FileDTO> files;
    private String chgUserId;
    private Date crtDate;
    private Date chgDate;
}
