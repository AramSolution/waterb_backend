package arami.common.userWeb.member.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import arami.common.adminWeb.site.service.SiteManageService;
import arami.common.userWeb.member.service.MemberUsrService;
import arami.common.userWeb.member.service.MemberUsrDAO;

@Service("memberUsrService")
public class MemberUsrServiceImpl extends EgovAbstractServiceImpl implements MemberUsrService {

	@Resource(name = "memberUsrDAO")
	private MemberUsrDAO memberUsrDAO;

    @Override
    public List<Object> selectUserMemberList(Object object) throws Exception {
        return memberUsrDAO.selectUserMemberList(object);
    }

    @Override
    public int selectUserMemberListCount(Object object) throws Exception {
        return memberUsrDAO.selectUserMemberListCount(object);
    }

    @Override
    public String checkMemberId(Object object) throws Exception {
        return memberUsrDAO.checkMemberId(object);
    }

    @Override
    public String checkMemberId2(Object object) throws Exception {
        return memberUsrDAO.checkMemberId2(object);
    }

    @Override
    public int insertAdminRollAjax(Object object) throws Exception {
        return memberUsrDAO.insertAdminRollAjax(object);
    }

    @Override
    public int insertAdminMemberAjax(Object object) throws Exception {
        return memberUsrDAO.insertAdminMemberAjax(object);
    }
}
