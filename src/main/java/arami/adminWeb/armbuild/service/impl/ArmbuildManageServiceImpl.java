package arami.adminWeb.armbuild.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

import egovframework.com.cmm.EgovMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arami.adminWeb.armbuild.service.ArmbuildManageDAO;
import arami.adminWeb.armbuild.service.ArmbuildManageService;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildDeleteParam;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildInsertBatchRequest;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildInsertRequest;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildListRequest;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildInsertBatchResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildListItemResponse;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildResultResponse;
import egovframework.com.cmm.LoginVO;

@Service("armbuildManageService")
public class ArmbuildManageServiceImpl extends EgovAbstractServiceImpl implements ArmbuildManageService {

	@Resource(name = "armbuildManageDAO")
	private ArmbuildManageDAO armbuildManageDAO;

	@Resource(name = "egovMessageSource")
	private EgovMessageSource egovMessageSource;

	@Override
	public List<ArmbuildListItemResponse> selectArmbuildList(ArmbuildListRequest request) {
		request.setGubun1(request.getGubun1().trim());
		request.setGubun2(request.getGubun2().trim());
		List<ArmbuildListItemResponse> list = armbuildManageDAO.selectArmbuildList(request);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	@Transactional
	public ArmbuildInsertBatchResponse saveArmbuildBatch(ArmbuildInsertBatchRequest batch) {
		String chgUserId = resolveChgUserId();
		List<String> buildIds = new ArrayList<>();
		for (ArmbuildInsertRequest request : batch.getItems()) {
			applyTrimmedFields(request);
			request.setChgUserId(chgUserId);
			String bid = request.getBuildId() != null ? request.getBuildId().trim() : "";
			if (!bid.isEmpty()) {
				ArmbuildDeleteParam idParam = new ArmbuildDeleteParam(bid);
				if (armbuildManageDAO.countArmbuildActiveByBuildId(idParam) == 0) {
					throw new IllegalArgumentException("건축물용도 수정에 실패했습니다.(BUILD_ID NotFind) BUILD_ID=" + bid);
				}
				int updated = armbuildManageDAO.updateArmbuild(request);
				if (updated == 0) {
					throw new IllegalStateException("건축물용도 수정에 실패했습니다. BUILD_ID=" + bid);
				}
				buildIds.add(bid);
			} else {
				request.setBuildId(armbuildManageDAO.getNextBuildId());
				armbuildManageDAO.insertArmbuild(request);
				buildIds.add(request.getBuildId());
			}
		}
		ArmbuildInsertBatchResponse response = new ArmbuildInsertBatchResponse();
		response.setResult("00");
		response.setMessage(egovMessageSource.getMessage("success.common.process"));
		response.setBuildIds(buildIds);
		return response;
	}

	private static void applyTrimmedFields(ArmbuildInsertRequest request) {
		if (request.getBuildId() != null) {
			request.setBuildId(request.getBuildId().trim());
		}
		request.setGubun1(request.getGubun1().trim());
		request.setGubun2(request.getGubun2().trim());
		request.setBuildNm(request.getBuildNm().trim());
		if (request.getBuildDesc() != null) {
			request.setBuildDesc(request.getBuildDesc().trim());
		}
	}

	@Override
	@Transactional
	public ArmbuildResultResponse deleteArmbuild(String buildId) {
		ArmbuildResultResponse response = new ArmbuildResultResponse();
		if (buildId == null || buildId.isBlank()) {
			response.setResult("40");
			response.setMessage("건축물용도 ID가 올바르지 않습니다.");
			return response;
		}
		int updated = armbuildManageDAO.updateArmbuildLogicalDelete(new ArmbuildDeleteParam(buildId.trim()));
		if (updated == 0) {
			response.setResult("01");
			response.setMessage("해당 건축물용도를 찾을 수 없습니다.");
			return response;
		}
		response.setResult("00");
		response.setMessage(egovMessageSource.getMessage("success.common.delete"));
		return response;
	}

	private static String resolveChgUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof LoginVO loginVO) {
			String uniqId = loginVO.getUniqId();
			return uniqId != null ? uniqId.trim() : "";
		}
		return "";
	}
}
