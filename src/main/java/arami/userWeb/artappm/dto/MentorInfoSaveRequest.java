package arami.userWeb.artappm.dto;

import lombok.Data;

/**
 * 사용자웹 멘토정보(ARTADVI) 저장 요청.
 * 멘토일지에서 상담장소·상담시간·상담내용·첨부파일(fileId)만 수정. reqId 필수.
 */
@Data
public class MentorInfoSaveRequest {
    /** ARTADVI PK (신청 요청 ID) */
    private String reqId;
    /** 상담일자(ADV_DT, yyyy-MM-dd) */
    private String advDt;
    /** 상담 시작 시각 (예: 10:00:00 또는 yyyy-MM-ddThh:mm) */
    private String advFrom;
    /** 상담 종료 시각 */
    private String advTo;
    /** 상담 장소 */
    private String advSpace;
    /** 상담 내용 */
    private String advDesc;
    /** 첨부파일 그룹 ID (빈 문자열이면 비움, null이면 기존 유지) */
    private String fileId;
    /** true면 ARTADVI만 저장하고 ARTAPPM.STTUS_CODE는 변경하지 않음 (임시저장) */
    private Boolean tempSave;
}
