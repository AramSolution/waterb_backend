package arami.common.auth.service;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.common.auth.service.impl.AuthRoleDTO;

import java.util.List;

@Repository("authRoleManageDAO")
public class AuthRoleManageDAO extends EgovAbstractMapper {

    public List<AuthRoleDTO> selectRolePattern() throws Exception {
        return selectList("authRoleManageDAO.selectRolePattern");
    }

}
