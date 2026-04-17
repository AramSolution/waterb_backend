package arami.adminWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * ARTPROD INSERT 한 건 파라미터 (DAO/MyBatis용)
 */
@Data
public class ArtprodScheduleItemInsertParam {

    private String proId;
    private Integer proSeq;
    /** 사업구분 (ARTPROM.PRO_GB, ARTPROD.PRO_GB) */
    private String proGb;
    /** 운영일자 (yyyy-MM-dd 문자열, SQL에서 DATE로 변환) */
    private String workDate;
    private String spaceId;
    private String startTime;
    private String endTime;
    /** 운영요일: 파이프 구분 "N|N|Y|Y|Y|Y|N" (일~토) */
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
