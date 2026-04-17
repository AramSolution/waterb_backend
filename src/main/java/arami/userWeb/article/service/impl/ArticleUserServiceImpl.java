package arami.userWeb.article.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import arami.common.adminWeb.article.service.ArticleManageService;
import arami.common.adminWeb.board.service.BoardMasterService;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.userWeb.article.service.ArticleUserService;
import arami.userWeb.article.service.dto.response.ArticleDetailResponse;
import arami.userWeb.article.service.dto.response.ArticleFileItemResponse;
import arami.userWeb.article.service.dto.response.ArticleListItemResponse;
import arami.userWeb.article.service.dto.response.ArticleListResponse;
import arami.userWeb.article.service.dto.response.ArticleNavItemResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleDetailNavResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleListItemResponse;
import arami.userWeb.article.service.dto.response.ArchiveArticleListResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자웹 게시판 목록 조회 서비스 구현.
 * 기존 ArticleManageService(selectArticleList/selectArticleListCount) 재사용 후 DTO로 변환.
 */
@Slf4j
@Service("articleUserService")
public class ArticleUserServiceImpl implements ArticleUserService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int DEFAULT_OFFSET = 0;

    @Resource(name = "articleManageService")
    private ArticleManageService articleManageService;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource(name = "boardMasterService")
    private BoardMasterService boardMasterService;

    @Override
    public ArticleListResponse getArticleList(String bbsId, Integer limit, Integer offset,
                                               String searchCondition, String searchKeyword) {
        int limitVal = limit != null && limit > 0 ? limit : DEFAULT_LIMIT;
        int offsetVal = offset != null && offset >= 0 ? offset : DEFAULT_OFFSET;

        Map<String, Object> param = new HashMap<>();
        param.put("bbsId", bbsId);
        param.put("lengthPage", limitVal);
        param.put("startIndex", offsetVal);
        if (searchCondition != null && !searchCondition.isEmpty()) {
            param.put("searchCondition", searchCondition);
        }
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            param.put("searchKeyword", searchKeyword);
        }

        try {
            final boolean isQna = isBoardReplyEnabled(bbsId);
            int totalCount = isQna
                    ? articleManageService.selectArticleListParentOnlyCount(param)
                    : articleManageService.selectArticleListCount(param);
            List<EgovMap> list = isQna
                    ? articleManageService.selectArticleListParentOnly(param)
                    : articleManageService.selectArticleList(param);

            List<ArticleListItemResponse> data = new ArrayList<>();
            for (EgovMap row : list) {
                data.add(toItemResponse(row, isQna));
            }
            // 게시판 비밀글사용여부(SECRET_YN)가 N이면 글 단위 SECRET_AT와 관계없이 비밀글 미적용
            if (!isBoardSecretEnabled(bbsId)) {
                for (ArticleListItemResponse item : data) {
                    item.setSecretAt("N");
                }
            }

            return ArticleListResponse.builder()
                    .data(data)
                    .recordsTotal(totalCount)
                    .recordsFiltered(totalCount)
                    .build();
        } catch (Exception e) {
            log.warn("게시글 목록 조회 실패 bbsId={}", bbsId, e);
            throw new IllegalStateException("게시글 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public ArchiveArticleListResponse getArchiveArticleList(
            String bbsId, Integer limit, Integer offset, String searchKeyword, String searchCondition) {
        int limitVal = limit != null && limit > 0 ? limit : DEFAULT_LIMIT;
        int offsetVal = offset != null && offset >= 0 ? offset : DEFAULT_OFFSET;

        Map<String, Object> param = new HashMap<>();
        param.put("bbsId", bbsId);
        param.put("lengthPage", limitVal);
        param.put("startIndex", offsetVal);
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String cond =
                    searchCondition != null && !searchCondition.isBlank() ? searchCondition.trim() : "1";
            param.put("searchCondition", cond);
            param.put("searchKeyword", searchKeyword.trim());
        }

        try {
            Map<String, Object> totalParam = new HashMap<>();
            totalParam.put("bbsId", bbsId);
            int totalRegisteredCount = articleManageService.selectArticleListCount(totalParam);
            int totalCount = articleManageService.selectArticleListCount(param);
            List<EgovMap> list = articleManageService.selectArticleList(param);

            List<ArchiveArticleListItemResponse> data = new ArrayList<>();
            for (EgovMap row : list) {
                ArchiveListThumb thumb = resolveArchiveListThumb(row);
                data.add(
                    ArchiveArticleListItemResponse.builder()
                        .nttId(parseIntSafe(row.get("nttId")))
                        .bbsId(getStr(row, "bbsId"))
                        .nttSj(getStr(row, "nttSj"))
                        .nttCn(getStr(row, "nttCn"))
                        .nttImgFileId(thumb != null ? thumb.fileIdSeq : null)
                        .nttImgSaveNm(thumb != null ? thumb.saveNm : null)
                        .build()
                );
            }

            return ArchiveArticleListResponse.builder()
                    .data(data)
                    .totalRegisteredCount(totalRegisteredCount)
                    .recordsTotal(totalCount)
                    .recordsFiltered(totalCount)
                    .build();
        } catch (Exception e) {
            log.warn("아카이브 게시글 목록 조회 실패 bbsId={}", bbsId, e);
            throw new IllegalStateException("아카이브 게시글 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public ArticleDetailResponse getArticleDetail(String bbsId, Integer nttId) {
        if (bbsId == null || bbsId.isBlank() || nttId == null) {
            return null;
        }
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("bbsId", bbsId);
            param.put("nttId", nttId);
            EgovMap row = articleManageService.selectArticleDetailForUser(param);
            if (row == null) {
                return null;
            }
            articleManageService.updateViewCount(param);
            ArticleDetailResponse resp = toDetailResponse(row);
            if (resp != null && resp.getRdcnt() != null) {
                resp.setRdcnt(resp.getRdcnt() + 1);
            }
            // 게시판 비밀글사용여부(SECRET_YN)가 N이면 비밀글 미적용(비밀번호 불필요)
            if (resp != null && !isBoardSecretEnabled(bbsId)) {
                resp.setSecretAt("N");
            }
            if (resp != null) {
                param.put("noticeAt", resp.getNoticeAt() != null ? resp.getNoticeAt() : row.get("noticeAt"));
                boolean isNotice = "Y".equals(resp.getNoticeAt());
                EgovMap prevRow = isNotice
                        ? articleManageService.selectPrevArticle(param)
                        : articleManageService.selectPrevArticleWhenNonNotice(param);
                EgovMap nextRow = articleManageService.selectNextArticle(param);
                resp.setPrevArticle(toNavItem(prevRow));
                resp.setNextArticle(toNavItem(nextRow));
                fillAttacheFiles(resp);
                fillNttImgFiles(resp);
                if (isBoardReplyEnabled(bbsId) && "N".equals(resp.getAnswerAt())) {
                    fillReply(resp, param);
                }
            }
            return resp;
        } catch (Exception e) {
            log.warn("게시글 상세 조회 실패 bbsId={} nttId={}", bbsId, nttId, e);
            throw new IllegalStateException("게시글 상세 조회에 실패했습니다.", e);
        }
    }

    @Override
    public ArchiveArticleDetailNavResponse getArchiveArticleDetail(String bbsId, Integer nttId) {
        if (bbsId == null || bbsId.isBlank() || nttId == null) {
            return ArchiveArticleDetailNavResponse.builder().build();
        }
        try {
            ArticleDetailResponse current = getArticleDetail(bbsId, nttId);
            if (current == null) {
                return null;
            }

            Map<String, Object> navParam = new HashMap<>();
            navParam.put("bbsId", bbsId);
            navParam.put("nttId", nttId);

            EgovMap prevNav = articleManageService.selectPrevArticleWhenNonNotice(navParam);
            EgovMap nextNav = articleManageService.selectNextArticle(navParam);

            ArticleDetailResponse prev = loadArticleDetailWithoutViewCount(
                    bbsId, parseIntSafe(prevNav != null ? prevNav.get("nttId") : null));
            ArticleDetailResponse next = loadArticleDetailWithoutViewCount(
                    bbsId, parseIntSafe(nextNav != null ? nextNav.get("nttId") : null));

            return ArchiveArticleDetailNavResponse.builder()
                    .prevArchive(prev)
                    .currentArchive(current)
                    .nextArchive(next)
                    .build();
        } catch (Exception e) {
            log.warn("아카이브 상세(이전/현재/다음) 조회 실패 bbsId={} nttId={}", bbsId, nttId, e);
            throw new IllegalStateException("아카이브 상세 조회에 실패했습니다.", e);
        }
    }

    private ArticleDetailResponse loadArticleDetailWithoutViewCount(String bbsId, Integer nttId) throws Exception {
        if (bbsId == null || bbsId.isBlank() || nttId == null) return null;
        Map<String, Object> param = new HashMap<>();
        param.put("bbsId", bbsId);
        param.put("nttId", nttId);
        EgovMap row = articleManageService.selectArticleDetailForUser(param);
        if (row == null) return null;
        ArticleDetailResponse resp = toDetailResponse(row);
        if (resp == null) return null;
        if (!isBoardSecretEnabled(bbsId)) {
            resp.setSecretAt("N");
        }
        fillAttacheFiles(resp);
        fillNttImgFiles(resp);
        if (isBoardReplyEnabled(bbsId) && "N".equals(resp.getAnswerAt())) {
            fillReply(resp, param);
        }
        return resp;
    }

    private void fillReply(ArticleDetailResponse resp, Map<String, Object> param) {
        if (resp == null || param == null) return;
        try {
            EgovMap replyRow = articleManageService.selectReplyByParntscttId(param);
            if (replyRow == null) return;
            String content = getStr(replyRow, "replyNttCn");
            if (content == null) content = getStr(replyRow, "REPLY_NTT_CN");
            String date = getStr(replyRow, "replyNtcrDt");
            if (date == null) date = getStr(replyRow, "REPLY_NTCR_DT");
            String author = getStr(replyRow, "replyNtcrNm");
            if (author == null) author = getStr(replyRow, "REPLY_NTCR_NM");
            resp.setReplyContent(content);
            resp.setReplyDate(date);
            resp.setReplyNtcrNm(author);
        } catch (Exception e) {
            log.warn("fillReply failed for nttId={}", param.get("nttId"), e);
        }
    }

    /**
     * selectArticleList EgovMap에서 NTT_IMG_FILE_ID 컬럼 값 추출.
     * MyBatis mapUnderscoreToCamelCase·JDBC 메타데이터에 따라 키가 nttImgFileId, NTT_IMG_FILE_ID, ntt_img_file_id 등으로 달라질 수 있어 보완.
     */
    private String extractNttImgFileGroupIdFromListRow(EgovMap row) {
        if (row == null || row.isEmpty()) {
            return null;
        }
        String[] keys = { "nttImgFileId", "NTT_IMG_FILE_ID", "ntt_img_file_id", "nTTImgFileId" };
        for (String k : keys) {
            Object v = row.get(k);
            if (v != null) {
                String s = v.toString().trim();
                if (!s.isEmpty()) {
                    return s;
                }
            }
        }
        for (Object kObj : row.keySet()) {
            if (kObj == null) {
                continue;
            }
            Object v = row.get(kObj);
            if (v == null) {
                continue;
            }
            String nk = kObj.toString().replace("_", "").replace("-", "");
            if (nk.equalsIgnoreCase("nttimgfileid")) {
                String s = v.toString().trim();
                if (!s.isEmpty()) {
                    return s;
                }
            }
        }
        return null;
    }

    /** 아카이브 목록 썸네일: 최소 seq 1건의 "fileId:seq" + saveNm(캐시 무효화). */
    private static final class ArchiveListThumb {
        final String fileIdSeq;
        final String saveNm;

        ArchiveListThumb(String fileIdSeq, String saveNm) {
            this.fileIdSeq = fileIdSeq;
            this.saveNm = saveNm;
        }
    }

    /**
     * 아카이브 목록: NTT_IMG_FILE_ID 그룹에서 seq 최소 1건의 참조와 저장명.
     * 그룹 ID 없음·파싱 실패·파일 없음이면 null.
     */
    private ArchiveListThumb resolveArchiveListThumb(EgovMap row) {
        String groupId = extractNttImgFileGroupIdFromListRow(row);
        if (groupId == null || groupId.isBlank()) {
            return null;
        }
        try {
            long fid = Long.parseLong(groupId.trim());
            List<FileDTO> files = fileManageService.selectFileListByFileId(fid);
            if (files == null || files.isEmpty()) {
                return null;
            }
            FileDTO first = files.stream()
                    .min(Comparator.comparingInt(FileDTO::getSeq))
                    .orElse(null);
            if (first == null || first.getFileId() == null) {
                return null;
            }
            String ref = first.getFileId() + ":" + first.getSeq();
            return new ArchiveListThumb(ref, first.getSaveNm());
        } catch (NumberFormatException e) {
            log.warn("resolveArchiveListThumb: invalid NTT_IMG_FILE_ID={}", groupId);
            return null;
        } catch (Exception e) {
            log.warn("resolveArchiveListThumb: selectFileListByFileId failed groupId={}", groupId, e);
            return null;
        }
    }

    private void fillAttacheFiles(ArticleDetailResponse resp) {
        if (resp == null) return;
        String atchFileId = resp.getAtchFileId();
        if (atchFileId == null || atchFileId.isBlank()) {
            return;
        }
        try {
            long fileId = Long.parseLong(atchFileId.trim());
            List<FileDTO> fileList = fileManageService.selectFileListByFileId(fileId);
            List<ArticleFileItemResponse> list = new ArrayList<>();
            for (FileDTO f : fileList) {
                list.add(ArticleFileItemResponse.builder()
                        .fileId(f.getFileId() != null ? String.valueOf(f.getFileId()) : null)
                        .seq(f.getSeq())
                        .orgfNm(f.getOrgfNm())
                        .saveNm(f.getSaveNm())
                        .build());
            }
            resp.setAttacheFiles(list);
        } catch (NumberFormatException e) {
            log.warn("getArticleDetail: invalid atchFileId, skip attacheFiles. atchFileId={}", atchFileId);
        } catch (Exception e) {
            log.warn("getArticleDetail: selectFileListByFileId failed for atchFileId={}", atchFileId, e);
        }
    }

    /** NTT_IMG_FILE_ID(파일 그룹)에 속한 이미지 목록을 seq 순으로 채움. */
    private void fillNttImgFiles(ArticleDetailResponse resp) {
        if (resp == null) {
            return;
        }
        String groupId = resp.getNttImgFileId();
        if (groupId == null || groupId.isBlank()) {
            return;
        }
        try {
            long fid = Long.parseLong(groupId.trim());
            List<FileDTO> fileList = fileManageService.selectFileListByFileId(fid);
            if (fileList == null || fileList.isEmpty()) {
                return;
            }
            fileList.sort(Comparator.comparingInt(FileDTO::getSeq));
            List<ArticleFileItemResponse> list = new ArrayList<>();
            for (FileDTO f : fileList) {
                list.add(ArticleFileItemResponse.builder()
                        .fileId(f.getFileId() != null ? String.valueOf(f.getFileId()) : null)
                        .seq(f.getSeq())
                        .orgfNm(f.getOrgfNm())
                        .saveNm(f.getSaveNm())
                        .build());
            }
            resp.setNttImgFiles(list);
        } catch (NumberFormatException e) {
            log.warn("fillNttImgFiles: invalid nttImgFileId, skip. value={}", groupId);
        } catch (Exception e) {
            log.warn("fillNttImgFiles: selectFileListByFileId failed for nttImgFileId={}", groupId, e);
        }
    }

    private static ArticleNavItemResponse toNavItem(EgovMap row) {
        if (row == null) return null;
        Integer nttId = parseIntSafe(row.get("nttId"));
        String nttSj = getStr(row, "nttSj");
        if (nttId == null && (nttSj == null || nttSj.isEmpty())) return null;
        return ArticleNavItemResponse.builder()
                .nttId(nttId)
                .nttSj(nttSj != null ? nttSj : "")
                .build();
    }

    private static ArticleDetailResponse toDetailResponse(EgovMap row) {
        Integer nttId = parseIntSafe(row.get("nttId"));
        Long parntscttId = null;
        Object p = row.get("parntscttId");
        if (p != null && p instanceof Number) {
            parntscttId = ((Number) p).longValue();
        } else if (p != null && !p.toString().trim().isEmpty()) {
            try {
                parntscttId = Long.parseLong(p.toString().trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return ArticleDetailResponse.builder()
                .nttId(nttId)
                .bbsId(getStr(row, "bbsId"))
                .bbsNm(getStr(row, "bbsNm"))
                .parntscttId(parntscttId)
                .answerAt(getStr(row, "answerAt"))
                .nttSj(getStr(row, "nttSj"))
                .nttCn(getStr(row, "nttCn"))
                .nttImgFileId(getStr(row, "nttImgFileId") != null
                        ? getStr(row, "nttImgFileId")
                        : getStr(row, "NTT_IMG_FILE_ID"))
                .nttData1(getStr(row, "nttData1") != null ? getStr(row, "nttData1") : getStr(row, "NTT_DATA1"))
                .nttData2(getStr(row, "nttData2") != null ? getStr(row, "nttData2") : getStr(row, "NTT_DATA2"))
                .nttData3(getStr(row, "nttData3") != null ? getStr(row, "nttData3") : getStr(row, "NTT_DATA3"))
                .nttData4(getStr(row, "nttData4") != null ? getStr(row, "nttData4") : getStr(row, "NTT_DATA4"))
                .nttData5(getStr(row, "nttData5") != null ? getStr(row, "nttData5") : getStr(row, "NTT_DATA5"))
                .nttData6(getStr(row, "nttData6") != null ? getStr(row, "nttData6") : getStr(row, "NTT_DATA6"))
                .nttData7(getStr(row, "nttData7") != null ? getStr(row, "nttData7") : getStr(row, "NTT_DATA7"))
                .nttData8(getStr(row, "nttData8") != null ? getStr(row, "nttData8") : getStr(row, "NTT_DATA8"))
                .nttData9(getStr(row, "nttData9") != null ? getStr(row, "nttData9") : getStr(row, "NTT_DATA9"))
                .nttData10(getStr(row, "nttData10") != null ? getStr(row, "nttData10") : getStr(row, "NTT_DATA10"))
                .rdcnt(parseIntSafe(row.get("rdcnt")))
                .ntcrId(getStr(row, "ntcrId"))
                .ntcrNm(getStr(row, "ntcrNm"))
                .maskNtcrNm(getStr(row, "maskNtcrNm"))
                .ntcrDt(getStr(row, "ntcrDt"))
                .noticeAt(getStr(row, "noticeAt"))
                .secretAt(getStr(row, "secretAt"))
                .atchFileId(getStr(row, "atchFileId"))
                .ntcrStartDt(getStr(row, "ntcrStartDt"))
                .ntcrEndDt(getStr(row, "ntcrEndDt"))
                .answerCnt(parseIntSafe(row.get("answerCnt") != null ? row.get("answerCnt") : row.get("ANSWER_CNT")))
                .build();
    }

    private static String getStr(EgovMap row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : null;
    }

    private static ArticleListItemResponse toItemResponse(EgovMap row, boolean includeAnswerCnt) {
        Object nttIdObj = row.get("nttId");
        Integer nttId = null;
        if (nttIdObj != null) {
            if (nttIdObj instanceof Number) {
                nttId = ((Number) nttIdObj).intValue();
            } else {
                try {
                    nttId = Integer.parseInt(nttIdObj.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        Integer answerCnt = null;
        if (includeAnswerCnt) {
            Object ac = row.get("answerCnt") != null ? row.get("answerCnt") : row.get("ANSWER_CNT");
            answerCnt = parseIntSafe(ac);
        }
        Object noticeAtObj = row.get("noticeAt") != null ? row.get("noticeAt") : row.get("NOTICE_AT");
        String noticeAt = noticeAtObj != null ? noticeAtObj.toString() : null;
        Object ntcrNmObj = row.get("ntcrNm") != null ? row.get("ntcrNm") : row.get("NTCR_NM");
        String ntcrNm = ntcrNmObj != null ? ntcrNmObj.toString() : null;
        Object secretAtObj = row.get("secretAt") != null ? row.get("secretAt") : row.get("SECRET_AT");
        String secretAt = secretAtObj != null ? secretAtObj.toString() : null;
        return ArticleListItemResponse.builder()
                .nttId(nttId)
                .bbsId(row.get("bbsId") != null ? row.get("bbsId").toString() : null)
                .nttSj(row.get("nttSj") != null ? row.get("nttSj").toString() : null)
                .ntcrNm(ntcrNm)
                .ntcrDt(row.get("ntcrDt") != null ? row.get("ntcrDt").toString() : null)
                .rdcnt(parseIntSafe(row.get("rdcnt")))
                .noticeAt(noticeAt)
                .atchFileId(row.get("atchFileId") != null ? row.get("atchFileId").toString() : null)
                .answerCnt(answerCnt)
                .secretAt(secretAt)
                .build();
    }

    private static Integer parseIntSafe(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 게시판의 답글 사용 여부(ARMBORD.REPLY_YN)가 Y인지 여부.
     * 문의답변형 목록(본문만)/상세 답글 조회에 사용. N이거나 조회 실패 시 false.
     */
    private boolean isBoardReplyEnabled(String bbsId) {
        return "Y".equals(getBoardReplyYn(bbsId));
    }

    /**
     * 게시판의 비밀글 사용 여부(ARMBORD.SECRET_YN)가 Y인지 여부.
     * N이거나 조회 실패 시 false → 비밀글 미적용(비밀번호 불필요).
     */
    private boolean isBoardSecretEnabled(String bbsId) {
        return "Y".equals(getBoardSecretYn(bbsId));
    }

    /**
     * 게시판의 답글 사용 여부(ARMBORD.REPLY_YN) 반환. 조회 실패 시 "N".
     */
    private String getBoardReplyYn(String bbsId) {
        if (bbsId == null || bbsId.isBlank()) return "N";
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("bbsId", bbsId);
            EgovMap board = boardMasterService.selectBoardMasterDetail(param);
            if (board == null) return "N";
            Object v = board.get("REPLY_YN") != null ? board.get("REPLY_YN") : board.get("replyYn");
            return "Y".equals(v != null ? v.toString().trim() : null) ? "Y" : "N";
        } catch (Exception e) {
            log.warn("게시판 답글설정 조회 실패 bbsId={}, N 반환", bbsId, e);
            return "N";
        }
    }

    @Override
    public String getBoardSecretYn(String bbsId) {
        if (bbsId == null || bbsId.isBlank()) return "N";
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("bbsId", bbsId);
            EgovMap board = boardMasterService.selectBoardMasterDetail(param);
            if (board == null) return "N";
            Object v = board.get("SECRET_YN") != null ? board.get("SECRET_YN") : board.get("secretYn");
            return "Y".equals(v != null ? v.toString().trim() : null) ? "Y" : "N";
        } catch (Exception e) {
            log.warn("게시판 비밀글설정 조회 실패 bbsId={}, N 반환", bbsId, e);
            return "N";
        }
    }

    @Override
    public boolean confirmArticlePassword(String bbsId, Integer nttId, String password) {
        if (bbsId == null || bbsId.isBlank() || nttId == null || password == null || password.isBlank()) {
            return false;
        }
        try {
            ModelMap param = new ModelMap();
            param.addAttribute("bbsId", bbsId);
            param.addAttribute("nttId", nttId);
            param.addAttribute("password", password);
            int cnt = articleManageService.confirmPasswd(param);
            return cnt > 0;
        } catch (Exception e) {
            log.warn("confirmArticlePassword failed: bbsId={}, nttId={}", bbsId, nttId, e);
            return false;
        }
    }
}
