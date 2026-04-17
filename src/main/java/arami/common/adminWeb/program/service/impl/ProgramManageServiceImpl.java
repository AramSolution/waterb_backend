package arami.common.adminWeb.program.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import arami.common.adminWeb.program.service.ProgramManageDAO;
import arami.common.adminWeb.program.service.ProgramManageService;

@Service("ProgramManageService")
public class ProgramManageServiceImpl extends EgovAbstractServiceImpl implements ProgramManageService {

    @Resource(name = "ProgramManageDAO")
	private ProgramManageDAO programManageDAO;

	/**
     * 프로그램 목록 조회
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public List<Object> selectProgramList(Object object) throws Exception{
		return programManageDAO.selectProgramList(object);
	}

	@Override
	public int selectProgramCount(Object object) throws Exception{
		return programManageDAO.selectProgramCount(object);
	}

	@Override
	public List<Object> selectProgramExcelList(Object object) throws Exception{
		return programManageDAO.selectProgramExcelList(object);
	}

    /**
     * 프로그램 상세 조회
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public Object selectProgramDetail(Object object) throws Exception{
		return programManageDAO.selectProgramDetail(object);
	}

    /**
     * 프로그램 등록 가능 여부 : Y: 가능, N: 불가
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public String checkProgramIdAjax(Object object) throws Exception{
		return programManageDAO.checkProgramIdAjax(object);
	}

	/**
     * 프로그램 등록
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public int insertProgramAjax(Object object) throws Exception{
		return programManageDAO.insertProgramAjax(object);
	}

	/**
     * 프로그램 수정
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public int updateProgramAjax(Object object) throws Exception{
		return programManageDAO.updateProgramAjax(object);
	}

	/**
     * 프로그램 삭제
     * @param object
     * @return
     * @throws Exception
     */
	@Override
	public int deleteProgramAjax(Object object) throws Exception{
		return programManageDAO.deleteProgramAjax(object);
	}

}

