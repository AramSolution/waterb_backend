package arami.adminWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 지원사업 정보 DTO (목록/상세 공통)
 * selectArtpromList, selectArtpromDetail 조회 컬럼과 일치.
 * 날짜(recFromDd, recToDd)는 YYYYMMDD 문자열.
 */
@Data
public class ArtpromDTO {

    private String proId;
    private String proGb;
    private String proType;
    /** 사업유형명(01=교육, 02=지원사업, 03=홍보). 목록/상세/엑셀 조회 시 CASE로 매핑 */
    private String proTypeNm;
    /** 신청구분(학생|학부모|학원|멘토|학교 순서, 예: Y|N|N|N|N) */
    private String reqGb;
    private String proNm;
    private String proTargetNm;
    /** 기타내용 */
    private String etcNm;
    /** 담당부서 */
    private String proDepa;
    /** 담당자 */
    private String proCharge;
    /** 연락처 */
    private String proTel;
    /** 사업성격 */
    private String proPart;
    /** 희망사업 */
    private String eduGb;
    /** 기초생활수급자여부 (Y/N) */
    private String basicYn;
    /** 차상위계층여부 (Y/N) */
    private String poorYn;
    /** 한부모가족여부 (Y/N) */
    private String singleYn;
    private String proTarget;
    /** 사업기간(FROM) */
    private String proFromDd;
    /** 사업기간(TO) */
    private String proToDd;
    private String recFromDd;
    private String recToDd;
    private Integer recCnt;
    /** 문의처 */
    private String proEnquiry;
    /** 신청방법 */
    private String proHow;
    /** 사업장소 */
    private String proSpace;
    /** 지원인원 */
    private String proNum;
    /** 참가비용 */
    private String proCost;
    /** 홈페이지 */
    private String proPage;
    /** 디자인타입 */
    private String proDesign;
    private String proSum;
    private String proDesc;
    private String proFileId;
    private String fileId;
    /** 진행상태(01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). 목록/엑셀 조회 시 사용 */
    private String runSta;
    /** 진행상태명. 목록/엑셀 조회 시 CASE로 매핑 */
    private String runStaNm;
    private String sttusCode;
    /** 목록 조회 시에만 존재 (ROW_NUMBER) */
    private String rnum;
}
