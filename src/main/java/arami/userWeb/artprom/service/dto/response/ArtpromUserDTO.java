package arami.userWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업 상세 1건 DTO (사용자웹 - selectArtpromDetail 결과)
 */
@Data
public class ArtpromUserDTO {

    private String proId;
    private String proGb;
    private String proType;
    private String reqGb;
    private String proNm;
    private String proTargetNm;
    private String etcNm;
    private String eduGb;
    private String basicYn;
    private String poorYn;
    private String singleYn;
    private String proTarget;
    private String recFromDd;
    private String recToDd;
    private Integer recCnt;
    private String proSum;
    private String proDesc;
    /** 주관부서 표시명 — ARTPROM.PRO_DEPA를 LETTCCMMNDETAILCODE(CODE_ID=EDR007)로 풀어 `CODE_NM` */
    private String proDepa;
    private String proCharge;
    private String proTel;
    /** 사업성격 원본 플래그 (예: Y|Y|N|N|N) */
    private String proPart;
    /** 사업성격 표시명 (예: 교육/학습, 진로/진학) */
    private String proPartNm;
    private String proFromDd;
    private String proToDd;
    private String proEnquiry;
    private String proHow;
    private String proSpace;
    private String proNum;
    private String proCost;
    private String proPage;
    private String proDesign;
    private String proFileId;
    private String fileId;
    private String runSta;
    private String runStaNm;
    private String sttusCode;
    private String rnum;
}
