package arami.userWeb.mentorWork.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.userWeb.artprom.service.ArtpromUserService;
import arami.userWeb.artprom.service.dto.response.ArtpromMentorWorkProjectItem;
import arami.userWeb.mentorWork.service.MentorWorkService;
import arami.userWeb.mentorWork.service.dto.request.MentorWorkListRequest;
import arami.userWeb.mentorWork.service.dto.response.MentorWorkAdviceItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 멘토업무(mentorWork) 화면 API - 사용자웹 전용.
 * /userWeb/mentorWork?reqGbPosition=4 - 정보조회 아래 목록 등.
 */
@Tag(name = "멘토업무(사용자)", description = "사용자웹 - 멘토업무 목록 조회 API")
@RestController
@RequestMapping("/api/user/mentor-work")
@RequiredArgsConstructor
public class MentorWorkUserController {

    private final MentorWorkService mentorWorkService;
    private final ArtpromUserService artpromUserService;

    @Operation(summary = "멘토업무 화면 사업 목록", description = "/userWeb/mentorWork 사업명 검색 버튼용. 진행/완료 사업 목록.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/projects", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<ArtpromMentorWorkProjectItem>> getMentorWorkProjectList() throws Exception {
        List<ArtpromMentorWorkProjectItem> list = artpromUserService.getMentorWorkProjectList();
        return ResponseEntity.ok(list != null ? list : List.of());
    }

    @Operation(summary = "멘토업무 목록 조회", description = "/userWeb/mentorWork 조회 버튼 - 정보조회 아래 목록. proId, advEsntlId 필수. reqEsntlNm은 공백이 아닐 때만 조건 적용.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<MentorWorkAdviceItem>> getMentorWorkAdviceList(@RequestBody MentorWorkListRequest request) {
        List<MentorWorkAdviceItem> list = mentorWorkService.getMentorWorkAdviceList(request);
        return ResponseEntity.ok(list != null ? list : List.of());
    }
}
