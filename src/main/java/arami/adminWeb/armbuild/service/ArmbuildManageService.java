package arami.adminWeb.armbuild.service;

import java.util.List;

import arami.adminWeb.armbuild.service.dto.request.ArmbuildInsertRequest;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildListRequest;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildInsertResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildListItemResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildResultResponse;

public interface ArmbuildManageService {

	/**
	 * GUBUN1(중분류 코드), GUBUN2(소분류 코드)로 건축물용도 목록 조회
	 */
	List<ArmbuildListItemResponse> selectArmbuildList(ArmbuildListRequest request);

	/**
	 * 건축물용도 등록 (STTUS_CODE='A', CRT_DATE/CHG_DATE=NOW())
	 */
	ArmbuildInsertResponse insertArmbuild(ArmbuildInsertRequest request);

	/**
	 * 건축물용도 논리 삭제 (BUILD_ID 기준 STTUS_CODE = 'D')
	 */
	ArmbuildResultResponse deleteArmbuild(String buildId);
}
