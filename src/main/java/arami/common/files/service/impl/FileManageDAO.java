package arami.common.files.service.impl;

import arami.common.files.service.FileDTO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 파일 메타 정보 DB 접근 DAO.
 * MyBatis 매퍼 AramiFileManageDAO (FileManage_SQL_mysql.xml)와 연동하며
 * ARTFILE 테이블에 대한 insert/select/delete/update 수행.
 */
@Repository("aramiFileManageDAO")
public class FileManageDAO extends EgovAbstractMapper {

    /** MyBatis mapper namespace (FileManage_SQL_mysql.xml) */
    private static final String NAMESPACE = "AramiFileManageDAO";

    /** 파일 정보 1건 INSERT */
    public int insertFileInfo(FileDTO fileDTO) throws Exception {
        return insert(NAMESPACE + ".insertFileInfo", fileDTO);
    }

    /** fileId 그룹 내 MAX(SEQ) 조회 */
    public int selectFileMaxSeq(Long fileId) throws Exception {
        return selectOne(NAMESPACE + ".selectFileMaxSeq", fileId);
    }

    /** fileId로 파일 목록 조회 (STTUS_CODE='A', SEQ 순) */
    public List<FileDTO> selectFileListByFileId(Long fileId) throws Exception {
        return selectList(NAMESPACE + ".selectFileListByFileId", fileId);
    }

    /** 파일 1건 레코드 삭제 (서비스에서 디스크 삭제 후 호출) */
    int deleteFileRecord(FileDTO fileDTO) throws Exception {
        return delete(NAMESPACE + ".deleteFile", fileDTO);
    }

    /** fileId 그룹 전체 레코드 삭제 */
    int deleteFileGroupRecords(Long fileId) throws Exception {
        return delete(NAMESPACE + ".deleteFileGroup", fileId);
    }

    /** 파일 교체 시 메타 정보 UPDATE (경로, 저장명, 확장자, 크기 등) */
    int updateFileForReplace(FileDTO fileDTO) throws Exception {
        return update(NAMESPACE + ".updateFileForReplace", fileDTO);
    }

    /** 파일 메타만 수정 (FILE_DESC, UPLOAD_DTTM) */
    int updateFileMeta(FileDTO fileDTO) throws Exception {
        return update(NAMESPACE + ".updateFileMeta", fileDTO);
    }

    /** fileId 그룹에서 FROM_SEQ 행의 SEQ를 TO_SEQ로 변경 */
    int updateFileSeq(long fileId, int fromSeq, int toSeq) throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("fileId", fileId);
        p.put("fromSeq", fromSeq);
        p.put("toSeq", toSeq);
        return update(NAMESPACE + ".updateFileSeq", p);
    }
}
