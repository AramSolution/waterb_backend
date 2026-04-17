package arami.shared.neis.service;

import arami.shared.neis.dto.response.ClassInfoDTO;
import arami.shared.neis.dto.response.SchoolDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * NEIS 교육정보 개방 포털 schoolInfo API 호출 및 군산시 학교 필터·페이징 처리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NeisApiService {

    private static final String GUNSAN_FILTER = "군산";
    private static final int NEIS_MAX_PAGE_SIZE = 1000;

    @Value("${neis.api.key}")
    private String neisApiKey;

    @Value("${neis.api.schoolInfo.url}")
    private String neisSchoolInfoUrl;

    @Value("${neis.api.classInfo.url}")
    private String neisClassInfoUrl;

    @Value("${neis.api.atptOfcdcScCode}")
    private String atptOfcdcScCode;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 전북 전체 학교를 NEIS에서 한 번에 조회한 뒤, ORG_RDNMA에 "군산"이 포함된 학교만 필터링.
     */
    public List<SchoolDTO> getGunsanSchools() {
        try {
            String raw = callNeisSchoolInfo(1, NEIS_MAX_PAGE_SIZE);
            JsonNode root = objectMapper.readTree(raw);
            JsonNode schoolInfoArray = root.path("schoolInfo");
            if (!schoolInfoArray.isArray()) {
                return Collections.emptyList();
            }
            List<SchoolDTO> allRows = new ArrayList<>();
            for (JsonNode item : schoolInfoArray) {
                JsonNode rowArray = item.path("row");
                if (!rowArray.isArray()) {
                    continue;
                }
                for (JsonNode row : rowArray) {
                    SchoolDTO dto = objectMapper.treeToValue(row, SchoolDTO.class);
                    if (dto != null && dto.getOrgRdnma() != null && dto.getOrgRdnma().contains(GUNSAN_FILTER)) {
                        allRows.add(dto);
                    }
                }
            }
            return allRows;
        } catch (JsonProcessingException e) {
            log.warn("NEIS schoolInfo JSON 파싱 실패", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("NEIS schoolInfo 호출 실패", e);
            return Collections.emptyList();
        }
    }

    /**
     * 군산시 학교 목록을 페이징하여 반환. (0-based page)
     *
     * @param page 0-based 페이지 번호
     * @param size 페이지 크기
     * @param schoolNameKeyword 학교명 검색어 (null 또는 blank면 전체)
     */
    public org.springframework.data.domain.Page<SchoolDTO> getGunsanSchoolsWithPagination(
            int page, int size, String schoolNameKeyword) {
        List<SchoolDTO> gunsan = getGunsanSchools();
        if (schoolNameKeyword != null && !schoolNameKeyword.isBlank()) {
            String keyword = schoolNameKeyword.trim();
            gunsan = gunsan.stream()
                    .filter(s -> s.getSchulNm() != null && s.getSchulNm().contains(keyword))
                    .collect(Collectors.toList());
        }
        int total = gunsan.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<SchoolDTO> content = fromIndex < total ? gunsan.subList(fromIndex, toIndex) : Collections.emptyList();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }

    private String callNeisSchoolInfo(int pIndex, int pSize) {
        URI uri = UriComponentsBuilder.fromHttpUrl(neisSchoolInfoUrl)
                .queryParam("KEY", neisApiKey)
                .queryParam("Type", "json")
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize)
                .queryParam("ATPT_OFCDC_SC_CODE", atptOfcdcScCode)
                .build()
                .toUri();
        return restTemplate.getForObject(uri, String.class);
    }

    /**
     * NEIS classInfo API 호출: 해당 학교의 학년·반 목록 조회.
     *
     * @param atptOfcdcScCode 시도교육청코드 (예: P10)
     * @param sdSchulCode     학교코드 (예: 8342105)
     * @param pIndex          페이지 위치 (1-based)
     * @param pSize           페이지 당 건수
     * @return 학급(학년·반) 목록
     */
    public List<ClassInfoDTO> getClassInfo(String atptOfcdcScCode, String sdSchulCode, int pIndex, int pSize) {
        try {
            String raw = callNeisClassInfo(atptOfcdcScCode, sdSchulCode, pIndex, pSize);
            JsonNode root = objectMapper.readTree(raw);
            JsonNode classInfoArray = root.path("classInfo");
            if (!classInfoArray.isArray()) {
                return Collections.emptyList();
            }
            List<ClassInfoDTO> allRows = new ArrayList<>();
            for (JsonNode item : classInfoArray) {
                JsonNode rowArray = item.path("row");
                if (!rowArray.isArray()) {
                    continue;
                }
                for (JsonNode row : rowArray) {
                    ClassInfoDTO dto = objectMapper.treeToValue(row, ClassInfoDTO.class);
                    if (dto != null) {
                        allRows.add(dto);
                    }
                }
            }
            // 설계 원칙: 가장 최신 데이터 세트만 사용 (1순위 LOAD_DTM 최신, 2순위 AY 최대)
            String maxLoadDtm = allRows.stream()
                    .map(ClassInfoDTO::getLoadDtm)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .max(String::compareTo)
                    .orElse(null);
            if (maxLoadDtm != null) {
                allRows = allRows.stream()
                        .filter(d -> maxLoadDtm.equals(d.getLoadDtm()))
                        .collect(Collectors.toList());
            }
            int maxAy = allRows.stream()
                    .mapToInt(d -> parseIntSafe(d.getAy(), 0))
                    .max()
                    .orElse(0);
            allRows = allRows.stream()
                    .filter(d -> parseIntSafe(d.getAy(), 0) == maxAy)
                    .collect(Collectors.toList());
            // 학년·반 숫자 기준 오름차순 정렬 (1→2→…→10→11, "1반" 등 숫자만 추출하여 비교)
            allRows.sort(Comparator
                    .comparingInt((ClassInfoDTO d) -> parseDigitsOnly(d.getGrade(), 0))
                    .thenComparingInt((ClassInfoDTO d) -> parseDigitsOnly(d.getClassNm(), 0)));
            return allRows;
        } catch (JsonProcessingException e) {
            log.warn("NEIS classInfo JSON parse failed", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("NEIS classInfo call failed", e);
            return Collections.emptyList();
        }
    }

    private String callNeisClassInfo(String atptOfcdcScCode, String sdSchulCode, int pIndex, int pSize) {
        URI uri = UriComponentsBuilder.fromHttpUrl(neisClassInfoUrl)
                .queryParam("KEY", neisApiKey)
                .queryParam("Type", "json")
                .queryParam("pIndex", pIndex)
                .queryParam("pSize", pSize)
                .queryParam("ATPT_OFCDC_SC_CODE", atptOfcdcScCode)
                .queryParam("SD_SCHUL_CODE", sdSchulCode)
                .build()
                .toUri();
        return restTemplate.getForObject(uri, String.class);
    }

    /** 문자열을 정수로 파싱. 빈 값·비숫자면 defaultVal 반환. */
    private static int parseIntSafe(String value, int defaultVal) {
        if (value == null || value.isBlank()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /** 문자열에서 숫자만 추출 후 정수로 파싱 (예: "1반" → 1, "11반" → 11). 빈 결과·비숫자면 defaultVal. */
    private static int parseDigitsOnly(String value, int defaultVal) {
        if (value == null || value.isBlank()) {
            return defaultVal;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
