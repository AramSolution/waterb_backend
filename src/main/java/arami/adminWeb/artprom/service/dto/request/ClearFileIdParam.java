package arami.adminWeb.artprom.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 매퍼용 파라미터: FILE_ID / PRO_FILE_ID 초기화 시 PRO_ID 조건
 * (clearProFileId, clearFileId 등에서 사용, 키 추가 시 필드만 확장)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClearFileIdParam {

    private String proId;
}
