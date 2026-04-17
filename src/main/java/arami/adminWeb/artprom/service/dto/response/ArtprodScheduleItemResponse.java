package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업 일정 한 건 (ARTPROD) - 일정관리 화면용.
 * PRO_ID 기준 목록 조회 시 사용.
 */
@Data
public class ArtprodScheduleItemResponse {

    private String proId;
    private Integer proSeq;
    /** 운영일자 (WORK_DATE, yyyy-MM-dd) */
    private String workDate;
    /** 사업구분 코드 (PRO_GB) */
    private String proGb;
    /** 사업구분명 (EDR000 공통코드) */
    private String proGbNm;
    /** 장소 코드 (SPACE_ID) */
    private String spaceId;
    /** 장소명 (EDR004 공통코드) */
    private String spaceIdNm;
    /** 시작시간 "HH:mm" */
    private String startTime;
    /** 종료시간 "HH:mm" */
    private String endTime;
    /** 운영요일 7자 (일~토 순, Y/N). DB는 파이프 구분(N|N|Y|Y|Y|Y|N) → 쿼리에서 7자로 변환 */
    private String weekYn;
    /** 모집인원 등 (REC_CNT) */
    private String recCnt;
    /** 현재 신청 인원 수 (APPLY_CNT) */
    private Integer applyCnt;
    /** "신청인원/최대인원" 문자열 (예: 10/40) */
    private String applyCntStr;
    /** 항목1 (ITEM1) */
    private String item1;
    /** 항목2 (ITEM2) */
    private String item2;
    /** 항목3 (ITEM3) */
    private String item3;
    /** 항목4 (ITEM4) */
    private String item4;
    /** 사용여부 Y/N */
    private String useYn;
}
