package arami.common.adminWeb.board.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

@Repository("boardMasterDAO")
public class BoardMasterDAO extends EgovAbstractMapper {

	//게시판 종류 조회
	public List<Object> selectBBSMasterList(Object object){
        return selectList("boardMasterDAO.selectBBSMasterList", object);
    }

	//게시판 종류 카운트 조회
	public EgovMap selectBoardMasterDetail(Object object) throws Exception{
        return selectOne("boardMasterDAO.selectBoardMasterDetail", object);
    }

	public int selectBBSMasterListCount(Object object){
    	return selectOne("boardMasterDAO.selectBBSMasterListCount", object);
    }

	//게시판 유형 관련 공통 컬럼 조회
	public List<Object> selectCmmCodeDetail(Object object){
        return selectList("boardMasterDAO.selectCmmCodeDetail", object);
    }

	//다음 게시판 ID 채번
	public String getNextStringId(Object object){
        return selectOne("boardMasterDAO.getNextStringId", object);
    }

	//게시판 종류 등록
	public int insertBBSMaster(Object object) {
    	return insert("boardMasterDAO.insertBBSMaster", object);
    }

	//게시판관리 게시판 수정 페이지 조회
	public Object selectBBSMasterDetail(Object object){
        return selectOne("boardMasterDAO.selectBBSMasterDetail", object);
    }

	//게시판 종류 수정
	public int updateBBSMaster(Object object) {
    	return update("boardMasterDAO.updateBBSMaster", object);
    }

	//게시판 종류 삭제
	public int deleteBBSMaster(Object object){
    	return update("boardMasterDAO.deleteBBSMaster", object);
    }

	//게시판 엑셀 리스트 조회
	public List<Object> selectBBSMasterExcelList(Object object){
    	return selectList("boardMasterDAO.selectBBSMasterExcelList", object);
    }
}
