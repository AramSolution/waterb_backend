package arami.common.userWeb.member.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("memberUsrDAO")
public class MemberUsrDAO extends EgovAbstractMapper {

	public List<Object> selectUserMemberList(Object object) throws Exception{
        return selectList("memberUsrDAO.selectUserMemberList", object);
    }

	public int selectUserMemberListCount(Object object) throws Exception{
	    return selectOne("memberUsrDAO.selectUserMemberListCount", object);
	}

	public String checkMemberId(Object object) throws Exception{
	    return selectOne("memberUsrDAO.checkMemberId", object);
	}

	public String checkMemberId2(Object object) throws Exception{
	    return selectOne("memberUsrDAO.checkMemberId2", object);
	}

	public int insertAdminRollAjax(Object object) throws Exception{
	    return insert("memberUsrDAO.insertAdminRollAjax", object);
	}

	public int insertAdminMemberAjax(Object object) throws Exception{
	    return insert("memberUsrDAO.insertAdminMemberAjax", object);
	}
}
