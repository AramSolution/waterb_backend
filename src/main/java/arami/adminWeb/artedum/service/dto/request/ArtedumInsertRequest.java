package arami.adminWeb.artedum.service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) 등록 요청 DTO.
 * multipart/form-data 시 "data" 파트에 JSON으로 전달.
 */
@Data
public class ArtedumInsertRequest {

    /** 학원 ID (ARMUSER.ESNTL_ID) */
    @NotBlank(message = "학원 ID를 입력하세요")
    @Size(max = 20)
    private String eduEsntlId;

    /** 희망사업 (01=마중물스터디, 02=희망스터디) */
    @NotBlank(message = "희망사업을 선택하세요")
    @Size(max = 2)
    private String eduGb;

    /** 첨부파일 그룹 ID (파일 업로드 후 서버에서 설정) */
    private String eduFile;

    /** 진행상태 (01=임시저장, 02=신청, 03=승인, 04=반려, 05=정지, 99=취소) */
    @Size(max = 2)
    private String runSta;

    /** 최종변경자 (로그인 사용자 uniqId, 서버에서 설정 가능) */
    @Size(max = 20)
    private String chgUserId;

    /** 신청과목 목록 (ARTEDUD 여러 건) */
    @Valid
    private List<ArtedumSubjectItem> subjects;
}
