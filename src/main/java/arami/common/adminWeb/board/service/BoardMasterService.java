package arami.common.adminWeb.board.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface BoardMasterService {

	/**
     * 게시판 종류 조회
     * @param object
     * @return
     * @throws Exception
     */
    public List<Object> selectBBSMasterList(Object object) throws Exception;

    /**
     * 게시판 종류 카운트 조회
     * @param object
     * @return
     * @throws Exception
     */
    public int selectBBSMasterListCount(Object object) throws Exception;

    /**
     * 게시판 설정 상세 조회(master)
     * @param object
     * @return
     * @throws Exception
     */
	public EgovMap selectBoardMasterDetail(Object object) throws Exception;

	/**
     * 게시판 유형 관련 공통 컬럼 조회
     * @param object
     * @return
     * @throws Exception
     */
    public List<Object> selectCmmCodeDetail(Object object) throws Exception;

    /**
     * 다음 게시판 ID 채번
     * @param object
     * @return
     * @throws Exception
     */
    public String getNextStringId(Object object) throws Exception;

    /**
     * 게시판 종류 등록
     * @param object
     * @return
     * @throws Exception
     */
    public int insertBBSMaster(Object object) throws Exception;

    /**
     * 게시판관리 게시판수정 페이지 데이터 조회
     * @param object
     * @return
     * @throws Exception
     */
    public Object selectBBSMasterDetail(Object object) throws Exception;

    /**
     * 게시판 종류 수정
     * @param object
     * @return
     * @throws Exception
     */
    public int updateBBSMaster(Object object) throws Exception;

    /**
     * 게시판 종류 삭제
     * @param object
     * @return
     * @throws Exception
     */
    public int deleteBBSMaster(Object object) throws Exception;

    /**
     * 게시판 엑셀 리스트 조회
     * @param object
     * @return
     * @throws Exception
     */
    public List<Object> selectBBSMasterExcelList(Object object) throws Exception;

}