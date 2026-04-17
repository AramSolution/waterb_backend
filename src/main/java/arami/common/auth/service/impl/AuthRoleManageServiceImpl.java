package arami.common.auth.service.impl;


import arami.common.auth.service.AuthRoleManageService;
import arami.common.auth.service.AuthRoleManageDAO;
import arami.common.auth.service.MemberLoginService;
import egovframework.com.cmm.LoginVO;
import egovframework.let.utl.sim.service.EgovFileScrty;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("authRoleManageService")
public class AuthRoleManageServiceImpl extends EgovAbstractServiceImpl implements AuthRoleManageService {

    @Resource(name = "authRoleManageDAO")
    private AuthRoleManageDAO authRoleManageDAO;


    @Override
    public List<AuthRoleDTO> selectRolePattern() throws Exception {
        return authRoleManageDAO.selectRolePattern();
    }
}