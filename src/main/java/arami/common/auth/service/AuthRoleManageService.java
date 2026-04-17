package arami.common.auth.service;

import arami.common.auth.service.impl.AuthRoleDTO;

import java.util.List;

public interface AuthRoleManageService {

    public List<AuthRoleDTO> selectRolePattern() throws Exception;

}