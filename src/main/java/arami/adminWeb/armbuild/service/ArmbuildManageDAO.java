package arami.adminWeb.armbuild.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.armbuild.service.dto.request.ArmbuildDeleteParam;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildInsertRequest;
import arami.adminWeb.armbuild.service.dto.request.ArmbuildListRequest;
import arami.adminWeb.armbuild.service.dto.response.ArmbuildListItemResponse;

@Repository("armbuildManageDAO")
public class ArmbuildManageDAO extends EgovAbstractMapper {

	public List<ArmbuildListItemResponse> selectArmbuildList(ArmbuildListRequest request) {
		return selectList("armbuildManageDAO.selectArmbuildList", request);
	}

	public String getNextBuildId() {
		return selectOne("armbuildManageDAO.getNextBuildId", null);
	}

	public int insertArmbuild(ArmbuildInsertRequest request) {
		return insert("armbuildManageDAO.insertArmbuild", request);
	}

	public int countArmbuildActiveByBuildId(ArmbuildDeleteParam param) {
		Integer n = selectOne("armbuildManageDAO.countArmbuildActiveByBuildId", param);
		return n != null ? n : 0;
	}

	public int countArmbuildByBuildId(ArmbuildDeleteParam param) {
		Integer n = selectOne("armbuildManageDAO.countArmbuildByBuildId", param);
		return n != null ? n : 0;
	}

	public int updateArmbuild(ArmbuildInsertRequest request) {
		return update("armbuildManageDAO.updateArmbuild", request);
	}

	public int updateArmbuildLogicalDelete(ArmbuildDeleteParam param) {
		return update("armbuildManageDAO.updateArmbuildLogicalDelete", param);
	}
}
