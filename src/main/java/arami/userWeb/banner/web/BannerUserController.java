package arami.userWeb.banner.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import arami.adminWeb.banner.service.BannerManageService;
import arami.adminWeb.banner.service.dto.request.BannerListRequest;
import arami.adminWeb.banner.service.dto.response.BannerItemResponse;
import arami.adminWeb.banner.service.dto.response.BannerListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자웹 배너 조회 API.
 * 등록/수정/삭제는 관리자웹에서만 처리하며, 사용자웹은 조회만 제공한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/user/banner")
@RequiredArgsConstructor
public class BannerUserController {

    private final BannerManageService bannerManageService;

    @PostMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BannerListResponse> selectBannerList(@RequestBody(required = false) BannerListRequest request) {
        BannerListResponse response = new BannerListResponse();
        try {
            if (request == null) {
                request = new BannerListRequest();
            }
            int totalCount = bannerManageService.selectBannerListCount(request);
            List<BannerItemResponse> data = bannerManageService.selectBannerList(request);
            response.setData(data);
            response.setRecordsFiltered(totalCount);
            response.setRecordsTotal(totalCount);
            response.setResult("00");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("selectBannerList(user): {}", e.getMessage(), e);
            response.setResult("01");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
