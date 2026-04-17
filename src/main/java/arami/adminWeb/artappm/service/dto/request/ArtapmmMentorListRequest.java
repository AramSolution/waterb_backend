package arami.adminWeb.artappm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 지원사업(멘토) 목록 조회 요청 - PRO_ID 기준.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtapmmMentorListRequest {
    private String proId;
}
