package arami.common.userWeb.member.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface MemberUsrService {

    /**
     * 리스트 조회
     * @param object
     * @return
     * @throws Exception
     */
	public List<Object> selectUserMemberList(Object object) throws Exception;
	public int selectUserMemberListCount(Object object) throws Exception;

    public String checkMemberId(Object object) throws Exception;
    public String checkMemberId2(Object object) throws Exception;

    public int insertAdminRollAjax(Object object) throws Exception;
    public int insertAdminMemberAjax(Object object) throws Exception;
}
