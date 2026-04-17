package arami.shared.armuser.service;

import arami.shared.armuser.dto.request.AcademyListForUserRequest;
import arami.shared.armuser.dto.request.ArmuserDetailRequest;
import arami.shared.armuser.dto.request.ArmuserDeleteRequest;
import arami.shared.armuser.dto.request.ArmuserInsertRequest;
import arami.shared.armuser.dto.request.ArmuserListRequest;
import arami.shared.armuser.dto.request.ArmuserUpdateRequest;
import arami.shared.armuser.dto.response.AcademyListForUserItem;
import arami.shared.armuser.dto.response.ArmuserDTO;
import arami.shared.armuser.dto.response.ArmuserCrtfcDnValueCheckResponse;
import arami.shared.armuser.dto.response.ArmuserUserIdCheckResponse;
import arami.shared.armuser.dto.response.ArmuserResultResponse;

import java.util.List;

/**
 * ARMUSER(공통 사용자) Service 인터페이스
 */
public interface ArmuserService { 

    List<ArmuserDTO> selectList(ArmuserListRequest request);

    int selectListCount(ArmuserListRequest request);

    /** 사용자웹 학원조회: 정상 학원(ANR, P) 전부 + 과목(있으면 콤마, 없으면 공백) */
    List<AcademyListForUserItem> selectAcademyListForUserWeb(AcademyListForUserRequest request);

    int selectAcademyListForUserWebCount(AcademyListForUserRequest request);

    /** 엑셀 다운로드용 목록 조회 (검색 조건 적용, 페이징 없음) */
    List<ArmuserDTO> selectExcelList(ArmuserListRequest request);

    /** 본인인증 완료 시 수신한 DI(개인식별코드) - ARMUSER.CRTFC_DN_VALUE 조회 존재 여부 확인 */
    ArmuserCrtfcDnValueCheckResponse selectCrtfcDnValueCheck(String crtfcDnValue);

    /** 회원가입 시 아이디 중복 체크 - ARMUSER.USER_ID 조회 존재 여부 확인 */
    ArmuserUserIdCheckResponse selectUserIdCheck(String userId); 

    /** 상세 조회 */
    ArmuserDTO selectDetail(ArmuserDetailRequest request);

    /** userWeb 메인(학원)용 학원 상세 조회 */
    ArmuserDTO selectAcademyMainDetail(ArmuserDetailRequest request);

    /** 등록 (esntlId는 서버에서 항상 채번). 실패 시 결과 DTO 반환, 성공 시 null */
    ArmuserResultResponse insertArmuser(ArmuserInsertRequest request);

    /** 수정. 실패 시 결과 DTO 반환, 성공 시 null */
    ArmuserResultResponse updateArmuser(ArmuserUpdateRequest request);

    /** 회원 탈퇴(삭제). 실패 시 결과 DTO 반환, 성공 시 null */
    ArmuserResultResponse deleteArmuser(ArmuserDeleteRequest request);

    /** USER_PIC(프로필 사진) 1건 삭제: fileId+seq로 ARTFILE 1건 삭제 후, 해당 fileId에 남은 파일이 없으면 ARMUSER.USER_PIC 비움 */
    void deleteUserPic(String esntlId, Long fileId, Integer seq) throws Exception;

    /** ATTA_FILE(첨부파일) 1건 삭제: fileId+seq로 ARTFILE 1건 삭제 후, 해당 fileId에 남은 파일이 없으면 ARMUSER.ATTA_FILE 비움 */
    void deleteAttaFile(String esntlId, Long fileId, Integer seq) throws Exception;

    /** BIZNO_FILE(사업자등록증) 1건 삭제: fileId+seq로 ARTFILE 1건 삭제 후 ARMUSER.BIZNO_FILE 비움 */
    void deleteBiznoFile(String esntlId, Long fileId, Integer seq) throws Exception;
}
