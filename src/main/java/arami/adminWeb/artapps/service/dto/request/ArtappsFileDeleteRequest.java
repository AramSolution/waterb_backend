package arami.adminWeb.artapps.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 공부의명수 홍보파일/첨부파일 1건 삭제 요청 DTO
 * fileId + seq로 ARTFILE 1건 삭제 후, 해당 fileId에 남은 파일이 없으면 ARTPROM의 PRO_FILE_ID 또는 FILE_ID를 비움.
 */
@Data
public class ArtappsFileDeleteRequest {

    @NotBlank(message = "지원사업 ID(proId)는 필수입니다.")
    private String proId;

    @NotNull(message = "파일 ID(fileId)는 필수입니다.")
    private Long fileId;

    @NotNull(message = "파일 순번(seq)은 필수입니다.")
    private Integer seq;
}
