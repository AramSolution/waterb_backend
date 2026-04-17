package arami.userWeb.mentorWork.service.dto.request;

import lombok.Data;

/**
 * 멘토업무(mentorWork) 화면 - 조회 버튼 목록 API 요청.
 * proId, advEsntlId 필수. reqEsntlNm은 공백이 아닐 때만 조건 적용.
 */
@Data
public class MentorWorkListRequest {

    private String proId;
    private String advEsntlId;
    /** 신청자명 LIKE 검색. 공백이 아닐 때만 REQ_ESNTL_ID IN (이름 검색) 조건 적용 */
    private String reqEsntlNm;
}
