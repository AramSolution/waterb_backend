package arami.userWeb.article.web;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import arami.userWeb.article.service.ArticleUserService;
import arami.userWeb.article.service.dto.response.ArticleDetailResponse;
import arami.userWeb.article.service.dto.response.ArticleListResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleDetailNavResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 사용자웹 게시판(공지/지원사업 등) API.
 * /userWeb 메인(각 5건), /userWeb/community?tab=notice 등에서 동일 API 사용.
 */
@Tag(name = "게시글(사용자)", description = "사용자웹 - 공지/지원사업 등 게시글 목록 조회 API")
@RestController
@RequestMapping("/api/user/articles")
@RequiredArgsConstructor
public class ArticleUserController {

    /** ArticleManage_SQL_mysql selectArticleList: 1 통합, 2 제목, 3 작성자, 4 내용 */
    private static final Set<String> ARCHIVE_SEARCH_CONDITIONS = Set.of("1", "2", "3", "4");

    private final ArticleUserService articleUserService;

    @Operation(summary = "게시글 목록 조회", description = "bbsId별 게시글 목록(공지 우선, 노출기간·사용중만). limit/offset으로 페이징.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "bbsId 누락"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArticleListResponse> getArticles(
            @RequestParam String bbsId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String searchCondition,
            @RequestParam(required = false) String searchKeyword) {
        if (bbsId == null || bbsId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ArticleListResponse.builder()
                            .data(Collections.emptyList())
                            .recordsTotal(0)
                            .recordsFiltered(0)
                            .build());
        }
        ArticleListResponse response = articleUserService.getArticleList(bbsId, limit, offset, searchCondition, searchKeyword);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "아카이브 게시글 목록 조회", description = "사용자웹 아카이브 전용 목록. bbsId 기준, limit/offset 페이징. "
            + "searchKeyword가 있을 때 searchCondition: 1 통합(제목·작성자·내용), 2 제목, 3 작성자, 4 내용. 생략 시 1.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "bbsId 누락 또는 searchCondition 불가"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(path = { "/archive", "/archive/" }, produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArchiveArticleListResponse> getArchiveArticles(
            @RequestParam String bbsId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) String searchCondition) {
        if (bbsId == null || bbsId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ArchiveArticleListResponse.builder()
                            .data(Collections.emptyList())
                            .totalRegisteredCount(0)
                            .recordsTotal(0)
                            .recordsFiltered(0)
                            .build());
        }
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String cond = searchCondition == null ? "" : searchCondition.trim();
            if (!cond.isEmpty() && !ARCHIVE_SEARCH_CONDITIONS.contains(cond)) {
                return ResponseEntity.badRequest()
                        .body(ArchiveArticleListResponse.builder()
                                .data(Collections.emptyList())
                                .totalRegisteredCount(0)
                                .recordsTotal(0)
                                .recordsFiltered(0)
                                .build());
            }
        }
        ArchiveArticleListResponse response =
                articleUserService.getArchiveArticleList(bbsId, limit, offset, searchKeyword, searchCondition);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "bbsId·nttId로 1건 조회. 조회수 +1 후 반환.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "bbsId 또는 nttId 누락"),
        @ApiResponse(responseCode = "404", description = "게시글 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(path = { "/detail", "/detail/" }, produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArticleDetailResponse> getArticleDetail(
            @RequestParam String bbsId,
            @RequestParam Integer nttId) {
        if (bbsId == null || bbsId.isBlank() || nttId == null) {
            return ResponseEntity.badRequest().build();
        }
        ArticleDetailResponse detail = articleUserService.getArticleDetail(bbsId, nttId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "아카이브 게시글 상세 조회", description = "사용자웹 아카이브 상세(이전/현재/다음). bbsId·nttId로 조회(조회수 +1). "
            + "각 ArticleDetailResponse에 nttImgFileId(파일 그룹 ID)와 nttImgFiles(그룹 내 이미지 목록, seq 오름차순) 포함.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "bbsId 또는 nttId 누락"),
        @ApiResponse(responseCode = "404", description = "게시글 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(path = { "/archive/detail", "/archive/detail/" }, produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArchiveArticleDetailNavResponse> getArchiveArticleDetail(
            @RequestParam String bbsId,
            @RequestParam Integer nttId) {
        if (bbsId == null || bbsId.isBlank() || nttId == null) {
            return ResponseEntity.badRequest().build();
        }
        ArchiveArticleDetailNavResponse detail = articleUserService.getArchiveArticleDetail(bbsId, nttId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "게시판 설정 조회", description = "bbsId별 비밀글 사용 여부(secretYn). 글쓰기 화면에서 비밀번호 입력란 노출 여부 판단용.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "bbsId 누락"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(path = { "/board-settings", "/board-settings/" }, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, String>> getBoardSettings(@RequestParam String bbsId) {
        if (bbsId == null || bbsId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String secretYn = articleUserService.getBoardSecretYn(bbsId);
        return ResponseEntity.ok(Collections.singletonMap("secretYn", secretYn));
    }

    @Operation(summary = "비밀글 비밀번호 확인", description = "bbsId·nttId·password로 비밀번호 일치 여부 확인. 성공 시 상세 조회 가능.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공(일치/불일치 모두 200, body.success로 구분)"),
        @ApiResponse(responseCode = "400", description = "파라미터 누락"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(path = { "/confirm-password", "/confirm-password/" }, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Boolean>> confirmPassword(@RequestBody Map<String, Object> body) {
        String bbsId = body != null && body.get("bbsId") != null ? body.get("bbsId").toString() : null;
        Object nttIdObj = body != null ? body.get("nttId") : null;
        Integer nttId = null;
        if (nttIdObj instanceof Number) {
            nttId = ((Number) nttIdObj).intValue();
        } else if (nttIdObj != null && !nttIdObj.toString().isBlank()) {
            try {
                nttId = Integer.parseInt(nttIdObj.toString());
            } catch (NumberFormatException ignored) { }
        }
        String password = body != null && body.get("password") != null ? body.get("password").toString() : null;
        if (bbsId == null || bbsId.isBlank() || nttId == null || password == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("success", false));
        }
        boolean success = articleUserService.confirmArticlePassword(bbsId, nttId, password);
        return ResponseEntity.ok(Collections.singletonMap("success", success));
    }
}
