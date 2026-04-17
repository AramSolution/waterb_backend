package arami.adminWeb.artedum.service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) 수정 요청 DTO.
 * multipart/form-data 시 "data" 파트에 JSON. eduEsntlId, eduGb는 path에서 설정.
 */
@Data
public class ArtedumUpdateRequest {

    @NotBlank(message = "학원 ID를 입력하세요")
    @Size(max = 20)
    private String eduEsntlId;

    @NotBlank(message = "희망사업을 선택하세요")
    @Size(max = 2)
    private String eduGb;

    /** 첨부파일 그룹 ID (파일 처리 후 서버에서 설정, 기존 유지 또는 append 후 동일 ID) */
    private String eduFile;

    /** 진행상태 */
    @Size(max = 2)
    private String runSta;

    @Size(max = 20)
    private String chgUserId;

    /** 과목 목록: seq 있으면 UPDATE, 없으면 INSERT. 요청에 없는 기존 seq는 논리삭제(D) */
    @Valid
    private List<ArtedumSubjectItem> subjects;
}
