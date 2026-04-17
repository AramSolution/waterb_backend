package arami.userWeb.artprom.service.dto.request;

import lombok.Data;

/**
 * 내가 신청/임시저장한 지원사업 목록 조회 요청 (MY PAGE 신청현황)
 * reqEsntlId, userSe는 서비스에서 로그인 사용자로 설정.
 * 학생(SNR)=REQ_ESNTL_ID 기준, 학부모(PNR)=P_ESNTL_ID 기준으로 조회.
 */
@Data
public class ArtpromUserMyAppliedListRequest {

    /** 로그인 사용자 고유ID */
    private String reqEsntlId;
    /** 로그인 사용자 구분: SNR=학생, PNR=학부모, ANT=학원, MNR=멘토. 학생은 REQ_ESNTL_ID, 학부모는 P_ESNTL_ID로 조회 */
    private String userSe;
    /** 상태 필터: 01=임시저장, 02=신청, 03=승인, 04=반려, 05=중단, 99=취소. 비어 있으면 전체 */
    private String sttusCode;
}
