package arami.shared.armchil.service;

import arami.shared.armchil.dto.request.ArmchilLinkRequest;
import arami.shared.armchil.dto.response.ArmchilChildDTO;
import arami.shared.armuser.dto.response.ArmuserResultResponse;

import java.util.List;

/**
 * ARMCHIL(자녀관리) Service
 */
public interface ArmchilService {

    /**
     * 학부모의 자녀 목록 조회
     * @param pEsntlId 학부모 고유ID (P_ESNTL_ID)
     * @return 자녀 목록 (esntlId, userNm 등, userPicFiles 채움)
     */
    List<ArmchilChildDTO> getChildrenByParent(String pEsntlId);

    /**
     * 자녀의 학부모(보호자) 목록 조회
     * @param cEsntlId 자녀(학생) 고유ID (C_ESNTL_ID)
     * @return 보호자 목록 (esntlId, userNm 등, userPicFiles 채움)
     */
    List<ArmchilChildDTO> getParentsByChild(String cEsntlId);

    /**
     * 학부모의 자녀 목록 조회 - 엑셀 (파일 미포함)
     * @param pEsntlId 학부모 고유ID (P_ESNTL_ID)
     * @return 자녀 목록 (userPicFiles 미채움)
     */
    List<ArmchilChildDTO> getChildrenByParentExcel(String pEsntlId);

    /**
     * 자녀 연동 등록
     * @param pEsntlId 부모 고유ID (로그인 사용자 또는 관리자 지정)
     * @param request 학생명, 성별, 연락처, 주민등록번호
     * @return result 00=성공, 01=해당 자녀 없음, 50=이미 연동됨
     */
    ArmuserResultResponse linkChild(String pEsntlId, ArmchilLinkRequest request);

    /**
     * 자녀 연동 삭제 (부모·자식 ID로 ARMCHIL 1건 DELETE)
     */
    void deleteChildLink(String pEsntlId, String cEsntlId);
}
