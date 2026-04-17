package arami.adminWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 일정 저장 요청 한 건 (ARTPROD).
 * weekYn: 7자 (일~토) "NNYYYYN" 또는 파이프 구분 "N|N|Y|Y|Y|Y|N"
 * proSeq: 기존 일정이면 해당 PRO_SEQ, 신규면 null 또는 0.
 */
@Data
public class ArtprodScheduleItemSaveRequest {

    /** 기존 일정 PRO_SEQ (있으면 UPDATE, 없으면 INSERT) */
    private Integer proSeq;
    /** 운영일자 (yyyy-MM-dd 문자열, 없으면 빈 문자열 또는 null) */
    private String workDate;
    private String spaceId;
    private String startTime;
    private String endTime;
    private String weekYn;
    /** 모집인원 등 (REC_CNT) */
    private String recCnt;
    /** 항목1~4 (ITEM1~ITEM4) */
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String useYn;
}
