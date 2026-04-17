package arami.common.auth.service.impl;


import arami.common.auth.service.MemberLoginService;
import arami.common.auth.service.MemberLoginDAO;
import arami.common.auth.service.dto.OAuthJoinCheckResult;
import egovframework.com.cmm.LoginVO;
import egovframework.let.utl.sim.service.EgovFileScrty;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

@Service("memberLoginService")
public class MemberLoginServiceImpl extends EgovAbstractServiceImpl implements MemberLoginService {

    @Resource(name = "memberLoginDAO")
    private MemberLoginDAO memberLoginDAO;

    @Override
    public LoginVO actionLogin(LoginVO vo) throws Exception {
        // 1. 입력한 비밀번호를 암호화한다.
        String enpassword = EgovFileScrty.encryptPassword(vo.getPassword(), vo.getId());
        vo.setPassword(enpassword);

        // 2. 아이디와 암호화된 비밀번호가 DB와 일치하는지 확인한다.
        LoginVO loginVO = memberLoginDAO.actionLogin(vo);

        // 3. 결과를 리턴한다.
        if (loginVO != null && !loginVO.getId().equals("") && !loginVO.getPassword().equals("")) {
            return loginVO;
        } else {
            loginVO = new LoginVO();
        }

        return loginVO;
    }

    @Override
    public LoginVO actionLoginByOAuthEmail(String email, String userSe) throws Exception {
        return memberLoginDAO.actionLoginByOAuthEmail(email, userSe);
    }

    @Override
    public OAuthJoinCheckResult selectOAuthJoinCheck(String email, String userSe) throws Exception {
        return memberLoginDAO.selectOAuthJoinCheck(email, userSe);
    }

    @Override
    public String selectUserSeByOauthUserId(String userId) throws Exception {
        return memberLoginDAO.selectUserSeByOauthUserId(userId);
    }

    @Override
    public int updateOAuthId(String esntlId, String oauthGb, String naverAuthId, String kakaoAuthId) throws Exception {
        return memberLoginDAO.updateOAuthId(esntlId, oauthGb, naverAuthId, kakaoAuthId);
    }

    @Override
    public int unlinkOAuthId(String esntlId, String oauthService) throws Exception {
        return memberLoginDAO.unlinkOAuthId(esntlId, oauthService);
    }

    @Override
    public LoginVO actionLoginByEsntlId(String esntlId) throws Exception {
        return memberLoginDAO.actionLoginByEsntlId(esntlId);
    }

    @Override
    public String selectEsntlIdByNaverAuthIdExcluding(String naverAuthId, String excludeEsntlId) throws Exception {
        return memberLoginDAO.selectEsntlIdByNaverAuthIdExcluding(naverAuthId, excludeEsntlId);
    }

    @Override
    public String selectEsntlIdByKakaoAuthIdExcluding(String kakaoAuthId, String excludeEsntlId) throws Exception {
        return memberLoginDAO.selectEsntlIdByKakaoAuthIdExcluding(kakaoAuthId, excludeEsntlId);
    }

}