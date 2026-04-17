package arami.common.adminWeb.site.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface SiteManageService {

    /**
     * 사이트 등록
     * @param object
     * @return
     * @throws Exception
     */
	public int insertSite(Object object) throws Exception;

	/**
	 * 사이트 수정
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int updateSite(Object object) throws Exception;

	/**
	 * 사이트 삭제
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public int deleteSite(Object object) throws Exception;

	/**
	 * 사이트 목록 조회
	 * @param object
	 * @return
	 */
	public List<EgovMap> selectSiteList(Object object) throws Exception;

	public int selectSiteListCount(Object object) throws Exception;

	/**
	 * 사이트 상세 조회
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public EgovMap selectSiteDetail(Object object) throws Exception;

	/**
	 * 전체 사이트 조회
	 * @return
	 * @throws Exception
	 */
	public List<EgovMap> selectSiteSelectList() throws Exception;

}
