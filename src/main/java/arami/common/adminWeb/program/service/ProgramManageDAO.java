package arami.common.adminWeb.program.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("ProgramManageDAO")
public class ProgramManageDAO extends EgovAbstractMapper {

     /**
      * 프로그램 목록 조회
      * @param object
      * @return
      * @throws Exception
      */
      public List<Object> selectProgramList(Object object) throws Exception{
           return selectList("ProgramManageDAO.selectProgramList", object);
      }
 
      public int selectProgramCount(Object object) throws Exception{
           return selectOne("ProgramManageDAO.selectProgramCount", object);
      }
 
      public List<Object> selectProgramExcelList(Object object) throws Exception{
           return selectList("ProgramManageDAO.selectProgramExcelList", object);
      }
 
     /**
      * 프로그램 상세 조회
      * @param object
      * @return
      * @throws Exception
      */
      public Object selectProgramDetail(Object object) throws Exception{
           return selectOne("ProgramManageDAO.selectProgramDetail", object);
      }
 
     /**
      * 프로그램 등록 가능 여부 : Y: 가능, N: 불가
      * @param object
      * @return
      * @throws Exception
      */
      public String checkProgramIdAjax(Object object) throws Exception{
           return selectOne("ProgramManageDAO.checkProgramIdAjax", object);
      }
 
      /**
      * 프로그램 등록
      * @param object
      * @return
      * @throws Exception
      */
      public int insertProgramAjax(Object object) throws Exception{
           return insert("ProgramManageDAO.insertProgramAjax", object);
      }
 
      /**
      * 프로그램 수정
      * @param object
      * @return
      * @throws Exception
      */
      public int updateProgramAjax(Object object) throws Exception{
           return update("ProgramManageDAO.updateProgramAjax", object);
      }
 
      /**
      * 프로그램 삭제
      * @param object
      * @return
      * @throws Exception
      */
      public int deleteProgramAjax(Object object) throws Exception{
           return delete("ProgramManageDAO.deleteProgramAjax", object);
      }
 
 }
