package arami.adminWeb.artedum.service;

import arami.adminWeb.artedum.service.dto.request.ArtedumListRequest;
import arami.adminWeb.artedum.service.dto.response.ArtedumDTO;

import java.util.List;

/**
 * 가맹학원(희망사업 신청) — 사용자웹 목록 조회용 (관리자 CRUD API 제거 후).
 */
public interface ArtedumService {

    List<ArtedumDTO> selectList(ArtedumListRequest request);

    int selectListCount(ArtedumListRequest request);
}
