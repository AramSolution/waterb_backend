package arami.adminWeb.banner.web;

import java.util.List;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import arami.adminWeb.banner.service.BannerManageService;
import arami.adminWeb.banner.service.dto.request.BannerDetailRequest;
import arami.adminWeb.banner.service.dto.request.BannerListRequest;
import arami.adminWeb.banner.service.dto.request.BannerSaveRequest;
import arami.adminWeb.banner.service.dto.response.BannerDetailDataResponse;
import arami.adminWeb.banner.service.dto.response.BannerDetailResponse;
import arami.adminWeb.banner.service.dto.response.BannerItemResponse;
import arami.adminWeb.banner.service.dto.response.BannerListResponse;
import arami.adminWeb.banner.service.dto.response.BannerResultResponse;
import arami.common.CommonService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.service.EgovProperties;

@Slf4j
@RestController
@RequestMapping("/api/admin/banner")
public class BannerManageController extends CommonService {

    @Resource(name = "bannerManageService")
    private BannerManageService bannerManageService;

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    /**
     * 배너 목록 조회.
     * - 요청: JSON Body (startIndex, lengthPage, searchCondition, searchKeyword, banrGb, userGbn)
     * - 응답: 목록 데이터 + 전체 건수(recordsTotal/recordsFiltered)
     */
    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerListResponse> selectBannerList(@RequestBody BannerListRequest request) throws Exception {
        BannerListResponse response = new BannerListResponse();
        try {
            int totalCount = bannerManageService.selectBannerListCount(request);
            List<BannerItemResponse> data = bannerManageService.selectBannerList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectBannerList: {}", e.getMessage(), e);
            response.setResult("01");
            if ("true".equals(EgovProperties.getProperty("Globals.debug"))) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 배너 상세 조회.
     * - 요청: Path Variable(banrCd)
     * - 응답: 단건 상세 데이터
     */
    @GetMapping(value = "/{banrCd}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerDetailResponse> selectBannerDetail(@PathVariable String banrCd) throws Exception {
        BannerDetailResponse response = new BannerDetailResponse();
        try {
            BannerDetailRequest request = new BannerDetailRequest();
            request.setBanrCd(banrCd);
            BannerDetailDataResponse data = bannerManageService.selectBannerDetail(request);
            response.setData(data);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectBannerDetail: {}", e.getMessage(), e);
            response.setResult("01");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 배너 등록.
     * - 요청: multipart/form-data
     *   - data: BannerSaveRequest(JSON)
     *   - imageFile: 배너 이미지 파일(선택)
     * - 처리: 서비스에서 banrCd 채번 후 등록
     */
    @PostMapping(value = "/", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerResultResponse> insertBanner(
            @RequestPart("data") BannerSaveRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws Exception {
        BannerResultResponse response = new BannerResultResponse();
        try {
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isBlank()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            int result = bannerManageService.insertBanner(request, imageFile);
            response.setData(result);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.insert"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("insertBanner: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 배너 수정.
     * - 요청: multipart/form-data
     *   - Path Variable(banrCd)
     *   - data: BannerSaveRequest(JSON)
     *   - imageFile: 교체할 이미지 파일(선택)
     */
    @PutMapping(value = "/{banrCd}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerResultResponse> updateBanner(
            @PathVariable String banrCd,
            @RequestPart("data") BannerSaveRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws Exception {
        BannerResultResponse response = new BannerResultResponse();
        try {
            request.setBanrCd(banrCd);
            if (request.getUNIQ_ID() == null || request.getUNIQ_ID().isBlank()) {
                request.setUNIQ_ID(getCurrentUniqId());
            }
            int result = bannerManageService.updateBanner(request, imageFile);
            response.setData(result);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.update"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("updateBanner: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 배너 이미지 삭제.
     * - 요청: Path Variable(banrCd), Query Param(uniqId, 선택)
     * - 처리: 파일 그룹(ARTFILE) 삭제 후 배너 FILE_CD/IMG_URL 비움
     */
    @DeleteMapping(value = "/{banrCd}/image", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerResultResponse> deleteBannerImage(
            @PathVariable String banrCd,
            @RequestParam(name = "uniqId", required = false) String uniqId) throws Exception {
        BannerResultResponse response = new BannerResultResponse();
        try {
            String actor = (uniqId != null && !uniqId.isBlank()) ? uniqId : getCurrentUniqId();
            int result = bannerManageService.deleteBannerImage(banrCd, actor);
            response.setData(result);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteBannerImage: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 배너 삭제(소프트 삭제).
     * - 요청: Path Variable(banrCd), Query Param(uniqId, 선택)
     * - 처리: uniqId 미전달 시 현재 로그인 사용자 ID를 사용
     */
    @DeleteMapping(value = "/{banrCd}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerResultResponse> deleteBanner(
            @PathVariable String banrCd,
            @RequestParam(name = "uniqId", required = false) String uniqId) throws Exception {
        BannerResultResponse response = new BannerResultResponse();
        try {
            String actor = (uniqId != null && !uniqId.isBlank()) ? uniqId : getCurrentUniqId();
            int result = bannerManageService.deleteBanner(banrCd, actor);
            response.setData(result);
            response.setResult("00");
            response.setMessage(egovMessageSource.getMessage("success.common.delete"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("deleteBanner: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage(egovMessageSource.getMessage("fail.common.msg"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
