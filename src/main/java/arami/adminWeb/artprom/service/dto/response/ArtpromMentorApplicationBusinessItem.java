package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 멘토신청관리 화면 — 사업명(셀렉트)용 ARTPROM 요약 행.
 * REQ_GB 파이프 구분 4번째(멘토)가 Y, STTUS_CODE=A, RUN_STA=02 조건으로 조회.
 */
@Data
public class ArtpromMentorApplicationBusinessItem {

    /** 지원사업코드 */
    private String proId;
    /** 사업구분 */
    private String proGb;
    /** 사업구분명 (LETTCCMMNDETAILCODE EDR000) */
    private String proGbNm;
    /** 사업형태 */
    private String proType;
    /** 신청구분 (파이프 구분) */
    private String reqGb;
    /** 사업명 */
    private String proNm;
    /** 진행상태 */
    private String runSta;
    /** 상태 */
    private String sttusCode;
}
