package arami.userWeb.artprom.service.dto.response;

import lombok.Data;

/**
 * 멘토업무(mentorWork) 화면 - 사업명 검색 시 사업 목록 1건.
 * RUN_STA 04·05, req_gb 7번째 'Y', STTUS_CODE='A' 조건.
 */
@Data
public class ArtpromMentorWorkProjectItem {

    private String runSta;   // 04=진행, 05=완료
    private String runStaNm; // 진행/완료
    private String proId;
    private String proNm;
    private String reqDate; // "YYYY-MM-DD ~ YYYY-MM-DD"
}
