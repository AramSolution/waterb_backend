package arami.adminWeb.artprom.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 지원사업 등록 요청 DTO
 * multipart/form-data 시 "data" 파트에 JSON으로 전달. 날짜(recFromDd, recToDd)는 YYYYMMDD 문자열.
 */
@Data
public class ArtpromInsertRequest {

    /** 사업 ID (서버 채번, 클라이언트 미전송) */
    private String proId;
    private String proGb;
    private String proType;
    /** 신청구분(학생|학부모|학원|멘토|학교 순서, 예: Y|N|N|N|N) */
    @Size(max = 12)
    private String reqGb;
    @NotBlank(message = "사업명을 입력하세요")
    @Size(max = 200)
    private String proNm;
    @Size(max = 100)
    private String proTargetNm;
    /** 기타내용 */
    private String etcNm;
    /** 희망사업 */
    private String eduGb;
    /** 기초생활수급자여부 (Y/N) */
    private String basicYn;
    /** 차상위계층여부 (Y/N) */
    private String poorYn;
    /** 한부모가족여부 (Y/N) */
    private String singleYn;
    private String proTarget;
    @Size(max = 8)
    private String recFromDd;
    @Size(max = 8)
    private String recToDd;
    private Integer recCnt;
    // 홍보 상세(추가 컬럼)
    @Size(max = 512)
    private String proDepa;
    @Size(max = 128)
    private String proCharge;
    @Size(max = 128)
    private String proTel;
    @Size(max = 20)
    private String proPart;
    @Size(max = 8)
    private String proFromDd;
    @Size(max = 8)
    private String proToDd;
    private String proSum;
    private String proDesc;
    @Size(max = 1024)
    private String proEnquiry;
    @Size(max = 2048)
    private String proHow;
    @Size(max = 2048)
    private String proSpace;
    @Size(max = 1024)
    private String proNum;
    @Size(max = 1024)
    private String proCost;
    @Size(max = 512)
    private String proPage;
    @Size(max = 2)
    private String proDesign;
    /** 홍보파일 그룹 ID (파일 업로드 후 서버 설정) */
    private String proFileId;
    /** 첨부파일 그룹 ID (파일 업로드 후 서버 설정) */
    private String fileId;
    /** 진행상태(01=공고, 02=접수중, 03=검토중, 04=진행, 05=완료, 99=취소). INSERT RUN_STA */
    private String runSta;
    private String sttusCode;
    /** 최종변경자(로그인 사용자 uniqId). 없으면 세션에서 설정 */
    private String UNIQ_ID;
}
