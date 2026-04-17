package arami.common.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 파일 관리 서비스 인터페이스.
 * 파일 메타 정보의 DB 저장/조회/삭제/수정 및 디스크 파일 처리(FileUtil 연동)를 정의.
 * 게시글·첨부 등 다른 도메인에서 주입받아 사용 가능.
 */
public interface FileManageService {

    /**
     * 파일 메타 정보 1건 DB 등록.
     * @param fileDTO 저장할 파일 정보 (fileId, seq, filePath, saveNm 등)
     * @return insert 결과 행 수
     */
    int insertFileInfo(FileDTO fileDTO) throws Exception;

    /**
     * 해당 fileId 그룹 내 최대 seq 조회.
     * @param fileId 파일 그룹 ID
     * @return 최대 seq (없으면 0)
     */
    int selectFileMaxSeq(Long fileId) throws Exception;

    /**
     * fileId로 소속된 파일 목록 조회 (상태 정상만).
     * @param fileId 파일 그룹 ID
     * @return FileDTO 목록 (seq 순)
     */
    List<FileDTO> selectFileListByFileId(Long fileId) throws Exception;

    /**
     * 단일 파일 삭제. 디스크(원본+썸네일) 삭제 후 DB 레코드 삭제.
     * @param fileId 파일 그룹 ID
     * @param seq 파일 순번
     */
    void deleteFile(Long fileId, Integer seq) throws Exception;

    /**
     * 파일 그룹 전체 삭제. 해당 fileId 모든 파일을 디스크·DB에서 삭제.
     * @param fileId 파일 그룹 ID
     * @return 삭제 결과 (fileId, deletedCount)
     */
    Map<String, Object> deleteFileGroup(Long fileId) throws Exception;

    /**
     * 기존 파일을 새 파일로 교체. 새 파일 업로드 후 DB 갱신, 기존 파일 디스크 삭제.
     * @param fileId 파일 그룹 ID
     * @param seq 파일 순번
     * @param newFile 새 파일
     * @param atchFileId 저장 경로 구분용 (예: "FILE_")
     * @param storePath 저장 하위 경로 (null이면 "test")
     * @param fileDesc 파일 설명 (null 가능)
     * @param uploadDttm 업로드일시 (null이면 DB NOW())
     * @return 갱신된 파일 정보 DTO
     */
    FileDTO updateFile(Long fileId, Integer seq, MultipartFile newFile, String atchFileId, String storePath, String fileDesc, Date uploadDttm) throws Exception;

    /**
     * 파일 메타만 수정 (FILE_DESC, UPLOAD_DTTM). 파일 본체는 변경하지 않음.
     * @param fileId 파일 그룹 ID
     * @param seq 파일 순번
     * @param fileDesc 파일 설명 (null 가능)
     * @param uploadDttm 업로드일시 (null이면 DB NOW())
     */
    void updateFileMeta(Long fileId, Integer seq, String fileDesc, Date uploadDttm) throws Exception;

    /**
     * 동일 fileId 그룹 내 파일 순서를 재설정한다.
     * orderedCurrentSeqs.get(i) = 최종 i번 위치에 둘 **현재(변경 전)** SEQ 값.
     */
    void reorderFilesInGroup(Long fileId, List<Integer> orderedCurrentSeqs) throws Exception;

}
