package arami.adminWeb.artappm.service.dto.request;

import java.util.List;

import lombok.Data;

/**
 * 멘토 목록 저장 요청 (PUT /api/admin/artappm/{proId}/mentors).
 */
@Data
public class ArtapmmMentorSaveRequest {

    /** 지원사업 회차 (동일 화면 내 멘토는 같은 proSeq) */
    private Integer proSeq;
    private List<ArtapmmMentorItemSaveRequest> items;
}
