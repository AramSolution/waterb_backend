package arami.shared.neis.web;

import arami.shared.neis.dto.response.ClassInfoDTO;
import arami.shared.neis.dto.response.SchoolDTO;
import arami.shared.neis.service.NeisApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * NEIS 연동 REST API. 군산시 학교 목록 조회(페이징).
 */
@RestController
@RequestMapping("/api/neis")
@RequiredArgsConstructor
public class NeisController {

    private final NeisApiService neisApiService;

    /**
     * 군산시 학교 목록 페이징 조회.
     *
     * @param page 0-based 페이지 번호 (기본 0)
     * @param size 페이지 크기 (기본 15)
     * @param text 학교명 검색어 (선택, 포함 검색)
     */
    @GetMapping("/gunsan-schools")
    public ResponseEntity<Page<SchoolDTO>> getGunsanSchools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String text) {
        Page<SchoolDTO> result = neisApiService.getGunsanSchoolsWithPagination(page, size, text);
        return ResponseEntity.ok(result);
    }

    /**
     * 해당 학교 학년·반 목록 조회 (NEIS classInfo API).
     *
     * @param atptOfcdcScCode 시도교육청코드 (기본 P10)
     * @param sdSchulCode     학교코드 (필수, 예: 8342105)
     * @param pIndex          페이지 위치 1-based (기본 1)
     * @param pSize           페이지 당 건수 (기본 100)
     */
    @GetMapping("/class-info")
    public ResponseEntity<List<ClassInfoDTO>> getClassInfo(
            @RequestParam(defaultValue = "P10") String atptOfcdcScCode,
            @RequestParam String sdSchulCode,
            @RequestParam(defaultValue = "1") int pIndex,
            @RequestParam(defaultValue = "100") int pSize) {
        List<ClassInfoDTO> result = neisApiService.getClassInfo(atptOfcdcScCode, sdSchulCode, pIndex, pSize);
        return ResponseEntity.ok(result);
    }
}
