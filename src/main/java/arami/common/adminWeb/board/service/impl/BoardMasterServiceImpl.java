package arami.common.adminWeb.board.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.board.service.BoardMasterService;
import arami.common.adminWeb.board.service.BoardMasterDAO;

@Service("boardMasterService")
public class BoardMasterServiceImpl extends EgovAbstractServiceImpl implements BoardMasterService {

	@Resource(name = "boardMasterDAO")
	private BoardMasterDAO boardMasterDAO;

	//게시판 종류 조회
    @Override
    public List<Object> selectBBSMasterList(Object object) throws Exception {
        return boardMasterDAO.selectBBSMasterList(object);
    }

    //게시판 종류 카운트 조회
    @Override
    public EgovMap selectBoardMasterDetail(Object object) throws Exception {
        return boardMasterDAO.selectBoardMasterDetail(object);
    }

    @Override
    public int selectBBSMasterListCount(Object object) throws Exception {
    	 return boardMasterDAO.selectBBSMasterListCount(object);
    }

    //게시판 유형 관련 공통 컬럼 조회
    @Override
    public List<Object> selectCmmCodeDetail(Object object) throws Exception {
        return boardMasterDAO.selectCmmCodeDetail(object);
    }

    //다음 게시판 ID 채번
    @Override
    public String getNextStringId(Object object) throws Exception {
        return boardMasterDAO.getNextStringId(object);
    }

    //게시판 종류 등록
    @Override
    public int insertBBSMaster(Object object) throws Exception {
    	return boardMasterDAO.insertBBSMaster(object);
    }

    //게시판관리 게시판수정 페이지 데이터 조회
    @Override
    public Object selectBBSMasterDetail(Object object) throws Exception {
    	return boardMasterDAO.selectBBSMasterDetail(object);
    }

    //게시판 종류 수정
    @Override
    public int updateBBSMaster(Object object) throws Exception {
    	return boardMasterDAO.updateBBSMaster(object);
    }

    //게시판 종류 삭제
    @Override
    public int deleteBBSMaster(Object object) throws Exception {
    	return boardMasterDAO.deleteBBSMaster(object);
    }

    //게시판 엑셀 리스트 조회
    @Override
    public List<Object> selectBBSMasterExcelList(Object object) throws Exception {
    	return boardMasterDAO.selectBBSMasterExcelList(object);
    }

}
