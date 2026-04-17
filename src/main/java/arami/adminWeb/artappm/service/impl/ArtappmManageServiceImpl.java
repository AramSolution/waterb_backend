package arami.adminWeb.artappm.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import arami.shared.proc.dto.request.CheckRequest;
import arami.shared.proc.dto.response.CheckResponse;
import arami.shared.proc.service.ProcService;
import arami.adminWeb.artapps.service.ArtappsManageDAO;
import arami.adminWeb.artappm.service.ArtappmManageDAO;
import arami.adminWeb.artappm.service.ArtappmManageService;
import arami.adminWeb.artappm.service.dto.request.ArtappmClearFileIdParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmClearStudyCertParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ChangeListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmByStudentRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmInsertRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmFileDeleteRequest;
import arami.adminWeb.artappm.service.dto.request.ArtapmmApplicationByReqIdOnlyParam;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorApplicationRegisterRequest;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorDuplicateParam;
import arami.adminWeb.artappm.service.dto.request.ArtapmmMentorSaveParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmResultGbUpdateParam;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionListRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmSelectionUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmUpdateRequest;
import arami.adminWeb.artappm.service.dto.request.StudyCertDetailRequest;
import arami.adminWeb.artappm.service.dto.request.ArtappmStudyCertUpdateParam;
import arami.adminWeb.artappm.service.dto.response.ChangeListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmDTO;
import arami.adminWeb.artappm.service.dto.response.ArtapmmApplicationListItemResponse;
import arami.adminWeb.artappm.service.dto.response.ArtapmmDuplicateCheckResponse;
import arami.adminWeb.artappm.service.dto.response.ArtappmResultResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertDetailResponse;
import arami.adminWeb.artappm.service.dto.response.StudyCertListItemResponse;
import arami.userWeb.artappm.dto.UserMentorApplicationRegisterRequest;
import arami.userWeb.artprom.service.ArtpromUserDAO;
import arami.userWeb.artprom.service.dto.request.ArtpromUserDetailRequest;
import arami.userWeb.artprom.service.dto.response.ArtpromUserDTO;

/**
 * 지원사업 신청 관리 Service 구현
 */
@Slf4j
@Service("artappmManageService")
public class ArtappmManageServiceImpl extends EgovAbstractServiceImpl implements ArtappmManageService {

    private static final Logger log = LoggerFactory.getLogger(ArtappmManageServiceImpl.class);
    private static final String ARTAPPM_GUBUN = "01";
    private static final Map<String, String> ELIGIBILITY_LABELS = Map.of(
            "BASIC_YN", "기초생활수급자 여부",
            "POOR_YN", "차상위계층 여부",
            "SINGLE_YN", "한부모가족 여부"
    );

    @Resource(name = "artappmManageDAO")
    private ArtappmManageDAO artappmManageDAO;

    @Resource(name = "artappsManageDAO")
    private ArtappsManageDAO artappsManageDAO;

    @Resource(name = "fileManageService")
    private FileManageService fileManageService;

    @Resource(name = "procService")
    private ProcService procService;

    @Resource(name = "artpromUserDAO")
    private ArtpromUserDAO artpromUserDAO;

    @Override
    public String getNextReqId() throws Exception {
        return artappmManageDAO.getNextReqId();
    }

    @Override
    public List<ArtappmDTO> selectArtappmList(ArtappmListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappmManageDAO.selectArtappmList(request);
    }

    @Override
    public int selectArtappmListCount(ArtappmListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappmManageDAO.selectArtappmListCount(request);
    }

    @Override
    public List<ArtappmDTO> selectArtappmExcelList(ArtappmListRequest request) throws Exception {
        return artappmManageDAO.selectArtappmExcelList(request);
    }

    @Override
    public List<ArtappmDTO> selectArtappmSelectionList(ArtappmSelectionListRequest request) throws Exception {
        return artappmManageDAO.selectArtappmSelectionList(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArtappmSelectionBatch(List<ArtappmSelectionUpdateRequest> list, String chgUserId) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        String chgUser = chgUserId != null ? chgUserId : "";
        for (ArtappmSelectionUpdateRequest item : list) {
            if (!StringUtils.hasText(item.getReqId())) {
                continue;
            }
            ArtappmResultGbUpdateParam param = new ArtappmResultGbUpdateParam(
                    item.getReqId(),
                    item.getResultGb(),
                    chgUser
            );
            artappmManageDAO.updateArtappmResultGb(param);
        }
    }

    @Override
    public ArtappmDTO selectArtappmDetail(ArtappmDetailRequest request) throws Exception {
        return artappmManageDAO.selectArtappmDetail(request);
    }

    @Override
    public ArtappmDTO selectArtappmByStudentAndPro(ArtappmByStudentRequest request) throws Exception {
        return artappmManageDAO.selectArtappmByStudentAndPro(request);
    }

    @Override
    public boolean existsArtappmByPk(ArtappmInsertRequest request) throws Exception {
        if (request == null) {
            return false;
        }
        String proGb = request.getProGb() != null ? request.getProGb().trim() : "";
        // 03(공공형): (PRO_ID, PRO_SEQ, WORK_DT, REQ_ESNTL_ID)로 슬롯 중복만 차단
        if ("03".equals(proGb)) {
            return artappmManageDAO.countArtappmByPkWithWorkDt(request) > 0;
        }
        // 05/07(다회 신청): (PRO_ID, PRO_SEQ, REQ_ESNTL_ID)
        if ("05".equals(proGb) || "07".equals(proGb)) {
            return artappmManageDAO.countArtappmByPk(request) > 0;
        }
        // 그 외(1회 신청): (PRO_ID, REQ_ESNTL_ID)
        return artappmManageDAO.countArtappmByProIdReqEsntlId(request) > 0;
    }

    /**
     * f_check 결과가 N|... 형태일 때 실패 사유 문구 생성 (BASIC_YN → 기초생활수급자 여부 등)
     */
    private String buildEligibilityFailureMessage(String checkResult) {
        if (!StringUtils.hasText(checkResult) || !checkResult.startsWith("N|")) {
            return "자격 조건을 충족하지 않습니다.";
        }
        String part = checkResult.substring(2).trim();
        if (part.isEmpty()) {
            return "자격 조건을 충족하지 않습니다.";
        }
        String labels = Arrays.stream(part.split("\\|"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(code -> ELIGIBILITY_LABELS.getOrDefault(code, code))
                .collect(Collectors.joining(", "));
        return labels + " 조건을 충족하지 않습니다.";
    }

    /**
     * 자격 조건(f_check) 검사. 통과 시 null, 실패 시 반환할 ArtappmResultResponse.
     * proId/reqEsntlId가 없으면 f_check를 호출하지 않고 실패 반환(필수값 검증).
     */
    /** 자격검사 대상은 학생 ESNTL_ID (학부모 신청 시 cEsntlId, 기존 자가신청 시 reqEsntlId=학생) */
    private ArtappmResultResponse checkEligibility(String proId, String proGb, String studentEsntlId) {
        if (!StringUtils.hasText(proId) || !StringUtils.hasText(studentEsntlId)) {
            ArtappmResultResponse r = new ArtappmResultResponse();
            r.setResult("01");
            r.setMessage("필수 정보가 없습니다.");
            return r;
        }
        String gb = proGb != null ? proGb.trim() : "";
        log.debug("artappm f_check 호출: proId={}, proGb={}, studentEsntlId={}", proId, gb, studentEsntlId);
        CheckResponse checkResponse = procService.getCheck(new CheckRequest(ARTAPPM_GUBUN, gb, proId, studentEsntlId));
        if (checkResponse == null || !StringUtils.hasText(checkResponse.getResult())) {
            ArtappmResultResponse r = new ArtappmResultResponse();
            r.setResult("01");
            r.setMessage("시스템 오류가 발생했습니다.");
            return r;
        }
        String result = checkResponse.getResult().trim();
        if ("Y".equals(result)) {
            return null;
        }
        ArtappmResultResponse r = new ArtappmResultResponse();
        if ("E".equals(result)) {
            r.setResult("01");
            r.setMessage("시스템 오류가 발생했습니다.");
            return r;
        }
        r.setResult("02");
        r.setMessage(buildEligibilityFailureMessage(result));
        return r;
    }

    private static String resolveStudentEsntlIdForEligibility(ArtappmInsertRequest request) {
        if (request == null) {
            return "";
        }
        if (StringUtils.hasText(request.getCEsntlId())) {
            return request.getCEsntlId().trim();
        }
        return request.getReqEsntlId() != null ? request.getReqEsntlId().trim() : "";
    }

    private static String resolveStudentEsntlIdForEligibility(ArtappmUpdateRequest request, ArtappmDTO detailFallback) {
        if (request != null && StringUtils.hasText(request.getCEsntlId())) {
            return request.getCEsntlId().trim();
        }
        if (detailFallback != null && StringUtils.hasText(detailFallback.getCEsntlId())) {
            return detailFallback.getCEsntlId().trim();
        }
        if (detailFallback != null && StringUtils.hasText(detailFallback.getReqEsntlId())) {
            return detailFallback.getReqEsntlId().trim();
        }
        /* 복합키 수정 등: 상세 미조회 시 기존처럼 reqEsntlId=학생(자가신청). 학부모 신청은 cEsntlId 필수 */
        if (request != null && StringUtils.hasText(request.getReqEsntlId())) {
            return request.getReqEsntlId().trim();
        }
        return "";
    }

    private static String resolveProGbForEligibility(ArtappmInsertRequest request) {
        if (request == null || request.getProGb() == null) {
            return "";
        }
        return request.getProGb().trim();
    }

    private static String resolveProGbForEligibility(ArtappmUpdateRequest request, ArtappmDTO detailFallback) {
        if (request != null && StringUtils.hasText(request.getProGb())) {
            return request.getProGb().trim();
        }
        if (detailFallback != null && StringUtils.hasText(detailFallback.getProGb())) {
            return detailFallback.getProGb().trim();
        }
        return "";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArtappmResultResponse insertArtappm(ArtappmInsertRequest request) throws Exception {
        ArtappmResultResponse eligibilityFailure = checkEligibility(request.getProId(), resolveProGbForEligibility(request),
                resolveStudentEsntlIdForEligibility(request));
        if (eligibilityFailure != null) {
            return eligibilityFailure;
        }
        // PRO_GB 05(지역연계 진로체험), 07(글로벌 문화탐방): 강연/일정별 인원 마감 체크 (FOR UPDATE로 동시 신청 방지)
        if ("05".equals(request.getProGb()) || "07".equals(request.getProGb())) {
            String proId = request.getProId() != null ? request.getProId().trim() : "";
            String proSeq = request.getProSeq() != null ? request.getProSeq().trim() : "";
            if (StringUtils.hasText(proId) && StringUtils.hasText(proSeq) && !"0".equals(proSeq)) {
                Integer recCnt = artappmManageDAO.selectArtprodRecCntForUpdate(request);
                if (recCnt == null) {
                    ArtappmResultResponse r = new ArtappmResultResponse();
                    r.setResult("52");
                    r.setMessage("유효하지 않은 일정입니다.");
                    return r;
                }
                int currentCount = artappmManageDAO.countArtappmByProIdProSeq(request);
                if (currentCount >= recCnt) {
                    ArtappmResultResponse r = new ArtappmResultResponse();
                    r.setResult("51");
                    r.setMessage("신청 인원이 마감되었습니다.");
                    return r;
                }
            }
        }
        // 공공형 진로진학(proGb=03): PRO_SEQ가 비었을 때 reqDesc JSON의 consultPlaceTime으로 보정 (관리자·사용자 웹 동일)
        if ("03".equals(request.getProGb())) {
            String proSeq = request.getProSeq() != null ? request.getProSeq().trim() : "";
            if (!StringUtils.hasText(proSeq) || "0".equals(proSeq)) {
                String fromReqDesc = request.getReqDesc() != null ? request.getReqDesc().trim() : "";
                if (StringUtils.hasText(fromReqDesc) && fromReqDesc.startsWith("{")) {
                    try {
                        JsonNode node = new ObjectMapper().readTree(fromReqDesc);
                        JsonNode consultPlaceTime = node.path("consultPlaceTime");
                        if (!consultPlaceTime.isMissingNode() && !consultPlaceTime.isNull()) {
                            String val = consultPlaceTime.asText("");
                            if (StringUtils.hasText(val)) {
                                request.setProSeq(val);
                            }
                        }
                    } catch (Exception e) {
                        log.debug("reqDesc consultPlaceTime parse skip: {}", e.getMessage());
                    }
                }
            }
        }
        if (!StringUtils.hasText(request.getReqId())) {
            request.setReqId(artappmManageDAO.getNextReqId());
        }
        if (!StringUtils.hasText(request.getReqProSeq())) {
            request.setReqProSeq(request.getProSeq() != null ? request.getProSeq().trim() : "0");
        }
        artappmManageDAO.insertArtappm(request);
        return null;
    }

    @Override
    public ArtappmResultResponse updateArtappm(ArtappmUpdateRequest request) throws Exception {
        String proId = request.getProId();
        String reqEsntlId = request.getReqEsntlId();
        ArtappmDTO detailForEligibility = null;
        // UPDATE는 REQ_ID(PK) 기반으로만 수행한다. (03 등 다회 신청에서 복합키 selectOne은 2건 이상 매칭 위험)
        if (StringUtils.hasText(request.getReqId()) && (!StringUtils.hasText(proId) || !StringUtils.hasText(reqEsntlId))) {
            ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
            detailReq.setReqId(request.getReqId());
            ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(detailReq);
            if (detail == null) {
                ArtappmResultResponse r = new ArtappmResultResponse();
                r.setResult("01");
                r.setMessage("해당 지원사업 신청 건이 없습니다.");
                return r;
            }
            proId = detail.getProId();
            reqEsntlId = detail.getReqEsntlId();
            detailForEligibility = detail;
        } else if (StringUtils.hasText(request.getReqId()) && (!StringUtils.hasText(request.getCEsntlId()))) {
            ArtappmDetailRequest detailReq = new ArtappmDetailRequest();
            detailReq.setReqId(request.getReqId());
            detailForEligibility = artappmManageDAO.selectArtappmDetail(detailReq);
        }
        if (!StringUtils.hasText(request.getReqId())) {
            ArtappmResultResponse r = new ArtappmResultResponse();
            r.setResult("01");
            r.setMessage("필수 정보가 없습니다.");
            return r;
        }
        ArtappmResultResponse eligibilityFailure = checkEligibility(proId, resolveProGbForEligibility(request, detailForEligibility),
                resolveStudentEsntlIdForEligibility(request, detailForEligibility));
        if (eligibilityFailure != null) {
            return eligibilityFailure;
        }
        artappmManageDAO.updateArtappm(request);
        if ("08".equals(request.getProGb())) {
            artappsManageDAO.updateArtappsRowByReqId(request);
        }
        return null;
    }

    @Override
    public int deleteArtappm(ArtappmDeleteRequest request) throws Exception {
        return artappmManageDAO.deleteArtappm(request);
    }

    @Override
    public void deleteFile(ArtappmFileDeleteRequest request) throws Exception {
        fileManageService.deleteFile(request.getFileId(), request.getSeq());
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(request.getFileId());
        if (remaining.isEmpty() && StringUtils.hasText(request.getReqId())) {
            artappmManageDAO.clearFileId(new ArtappmClearFileIdParam(request.getReqId()));
        }
    }

    @Override
    public String getStudyCertFileId(String proId, String proSeq, String reqEsntlId) throws Exception {
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setProId(proId);
        req.setProSeq(proSeq != null && !proSeq.isBlank() ? proSeq : "0");
        req.setReqEsntlId(reqEsntlId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        return (detail != null && detail.getStudyCert() != null) ? detail.getStudyCert().trim() : null;
    }

    @Override
    public String getStudyCertFileIdByReqId(String reqId) throws Exception {
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setReqId(reqId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        return (detail != null && detail.getStudyCert() != null) ? detail.getStudyCert().trim() : null;
    }

    @Override
    public void uploadStudyCert(String proId, String proSeq, String reqEsntlId, String newFileId) throws Exception {
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setProId(proId);
        req.setProSeq(proSeq != null && !proSeq.isBlank() ? proSeq : "0");
        req.setReqEsntlId(reqEsntlId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        if (detail == null) {
            throw new IllegalArgumentException("해당 지원사업 신청 건이 없습니다.");
        }
        String currentStudyCert = detail.getStudyCert();
        if (!StringUtils.hasText(currentStudyCert)) {
            artappmManageDAO.updateArtappmStudyCert(new ArtappmStudyCertUpdateParam(
                    detail.getReqId(),
                    newFileId));
        }
    }

    @Override
    public void uploadStudyCertByReqId(String reqId, String newFileId) throws Exception {
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setReqId(reqId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        if (detail == null) {
            throw new IllegalArgumentException("해당 지원사업 신청 건이 없습니다.");
        }
        String currentStudyCert = detail.getStudyCert();
        if (!StringUtils.hasText(currentStudyCert)) {
            artappmManageDAO.updateArtappmStudyCert(new ArtappmStudyCertUpdateParam(detail.getReqId(), newFileId));
        }
    }

    @Override
    public List<StudyCertListItemResponse> selectStudyCertList(ArtappmListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappmManageDAO.selectStudyCertList(request);
    }

    @Override
    public int selectStudyCertListCount(ArtappmListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappmManageDAO.selectStudyCertListCount(request);
    }

    @Override
    public List<StudyCertListItemResponse> selectStudyCertListAll(ArtappmListRequest request) throws Exception {
        return artappmManageDAO.selectStudyCertListAll(request);
    }

    @Override
    public List<StudyCertListItemResponse> selectStudyCertExcelList(ArtappmListRequest request) throws Exception {
        request.setDefaultPaging();
        return artappmManageDAO.selectStudyCertExcelList(request);
    }

    @Override
    public StudyCertDetailResponse getStudyCertDetail(StudyCertDetailRequest request) throws Exception {
        return artappmManageDAO.selectStudyCertDetail(request);
    }

    @Override
    public void deleteStudyCert(String proId, String proSeq, String reqEsntlId, Integer seq) throws Exception {
        String pSeq = proSeq != null && !proSeq.isBlank() ? proSeq : "0";
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setProId(proId);
        req.setProSeq(pSeq);
        req.setReqEsntlId(reqEsntlId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        if (detail == null) {
            throw new IllegalArgumentException("해당 지원사업 신청 건이 없습니다.");
        }
        String studyCert = detail.getStudyCert();
        if (!StringUtils.hasText(studyCert)) {
            throw new IllegalArgumentException("수강확인증이 등록되어 있지 않습니다.");
        }
        long fileId = Long.parseLong(studyCert.trim());
        fileManageService.deleteFile(fileId, seq);
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
        if (remaining.isEmpty()) {
            artappmManageDAO.clearStudyCert(new ArtappmClearStudyCertParam(detail.getReqId()));
        }
    }

    @Override
    public void deleteStudyCertByReqId(String reqId, Integer seq) throws Exception {
        ArtappmDetailRequest req = new ArtappmDetailRequest();
        req.setReqId(reqId);
        ArtappmDTO detail = artappmManageDAO.selectArtappmDetail(req);
        if (detail == null) {
            throw new IllegalArgumentException("해당 지원사업 신청 건이 없습니다.");
        }
        String studyCert = detail.getStudyCert();
        if (!StringUtils.hasText(studyCert)) {
            throw new IllegalArgumentException("수강확인증이 등록되어 있지 않습니다.");
        }
        long fileId = Long.parseLong(studyCert.trim());
        fileManageService.deleteFile(fileId, seq);
        List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
        if (remaining.isEmpty()) {
            artappmManageDAO.clearStudyCert(new ArtappmClearStudyCertParam(detail.getReqId()));
        }
    }

    @Override
    public List<ChangeListItemResponse> getChangeList(String proId, String proSeq, String reqEsntlId) throws Exception {
        ChangeListRequest request = new ChangeListRequest();
        request.setAGubun(ARTAPPM_GUBUN);
        request.setProId(proId);
        request.setProSeq(proSeq != null && !proSeq.isBlank() ? proSeq : "0");
        request.setReqEsntlId(reqEsntlId);
        List<ChangeListItemResponse> list = artappmManageDAO.selectChangeList(request);
        if (list != null) {
            for (ChangeListItemResponse item : list) {
                if (item.getChgDesc() != null && !item.getChgDesc().isEmpty()) {
                    item.setChgDesc(item.getChgDesc().replaceFirst(",\\s*$", ""));
                }
            }
            return list;
        }
        return Arrays.asList();
    }

    @Override
    public ArtapmmDuplicateCheckResponse checkMentorApplicationDuplicate(String proId, Integer proSeq, String reqEsntlId) throws Exception {
        ArtapmmDuplicateCheckResponse r = new ArtapmmDuplicateCheckResponse();
        r.setResult("00");
        r.setDuplicate(false);
        if (!StringUtils.hasText(proId) || !StringUtils.hasText(reqEsntlId)) {
            return r;
        }
        ArtapmmMentorDuplicateParam dup = new ArtapmmMentorDuplicateParam();
        dup.setProId(proId.trim());
        dup.setProSeq(proSeq != null ? proSeq : 0);
        dup.setReqEsntlId(reqEsntlId.trim());
        int cnt = artappmManageDAO.countArtapmmActiveByProSeqReqEsntlId(dup);
        r.setDuplicate(cnt > 0);
        return r;
    }

    @Override
    public ArtapmmApplicationListItemResponse getArtapmmMentorApplicationDetailByReqId(String reqId) throws Exception {
        if (!StringUtils.hasText(reqId)) {
            return null;
        }
        ArtapmmApplicationByReqIdOnlyParam q = new ArtapmmApplicationByReqIdOnlyParam();
        q.setReqId(reqId.trim());
        return artappmManageDAO.selectArtapmmApplicationByReqId(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArtappmResultResponse registerMentorApplication(String proId, ArtapmmMentorApplicationRegisterRequest request, String chgUserId) throws Exception {
        if (proId == null || proId.trim().isEmpty()) {
            throw new IllegalArgumentException("proId is required");
        }
        if (request == null || !StringUtils.hasText(request.getReqEsntlId())) {
            ArtappmResultResponse r = new ArtappmResultResponse();
            r.setResult("01");
            r.setMessage("멘토(신청자)를 선택해 주세요.");
            return r;
        }
        Integer proSeq = request.getProSeq() != null ? request.getProSeq() : 0;

        ArtapmmMentorDuplicateParam dup = new ArtapmmMentorDuplicateParam();
        dup.setProId(proId.trim());
        dup.setProSeq(proSeq);
        dup.setReqEsntlId(request.getReqEsntlId().trim());
        if (artappmManageDAO.countArtapmmActiveByProSeqReqEsntlId(dup) > 0) {
            ArtappmResultResponse r = new ArtappmResultResponse();
            r.setResult("50");
            r.setMessage("동일 지원사업·회차에 이미 등록된 멘토 신청이 있습니다.");
            return r;
        }

        String newReqId = artappmManageDAO.getNextArtapmmReqId();

        ArtapmmMentorSaveParam param = new ArtapmmMentorSaveParam();
        param.setReqId(newReqId);
        param.setProId(proId.trim());
        param.setProSeq(proSeq);
        param.setReqEsntlId(request.getReqEsntlId().trim());
        param.setReqPlay(truncateToMax(emptyToNull(request.getReqPlay()), 2048));
        param.setReqDesc(truncateToMax(request.getReqDesc(), 2048));
        param.setFileId(request.getFileId() != null ? request.getFileId().trim() : "");
        param.setResultGb(StringUtils.hasText(request.getResultGb()) ? request.getResultGb().trim() : "N");
        param.setReqDt(StringUtils.hasText(request.getReqDt()) ? request.getReqDt().trim() : LocalDate.now().toString());
        param.setAprrDt(emptyToNull(request.getAprrDt()));
        param.setChgDt(emptyToNull(request.getChgDt()));
        param.setStopDt(emptyToNull(request.getStopDt()));
        param.setReaDesc(truncateToMax(request.getReaDesc(), 2048));
        param.setCollegeNm(truncateToMax(request.getCollegeNm(), 512));
        param.setLeaveYn(StringUtils.hasText(request.getLeaveYn()) ? request.getLeaveYn().trim() : "N");
        param.setMajorNm(truncateToMax(request.getMajorNm(), 512));
        param.setSchoolLvl(request.getSchoolLvl() != null ? request.getSchoolLvl() : 0);
        param.setStudentId(truncateToMax(request.getStudentId(), 64));
        param.setHschoolNm(truncateToMax(request.getHschoolNm(), 512));
        param.setReqReason(request.getReqReason());
        param.setCareer(request.getCareer());
        param.setReqSub(truncateToMax(emptyToNull(request.getReqSub()), 4));
        param.setJoinTime(truncateToMax(emptyToNull(request.getJoinTime()), 64));
        param.setAgree1Yn(normalizeYn(emptyToNull(request.getAgree1Yn())));
        param.setAgree2Yn(normalizeYn(emptyToNull(request.getAgree2Yn())));
        param.setSttusCode(StringUtils.hasText(request.getSttusCode()) ? request.getSttusCode().trim() : "A");
        param.setChgUserId(chgUserId != null ? chgUserId : "");

        artappmManageDAO.insertArtapmm(param);
        ArtappmResultResponse ok = new ArtappmResultResponse();
        ok.setResult("00");
        ok.setReqId(newReqId);
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArtappmResultResponse registerMentorApplicationForUser(
            String proId,
            UserMentorApplicationRegisterRequest request,
            String currentEsntlId,
            String userSe,
            String chgUserId) throws Exception {
        ArtappmResultResponse err = new ArtappmResultResponse();
        if (!StringUtils.hasText(proId)) {
            err.setResult("01");
            err.setMessage("사업코드가 필요합니다.");
            return err;
        }
        if (request == null) {
            err.setResult("01");
            err.setMessage("신청 정보가 필요합니다.");
            return err;
        }
        if (!StringUtils.hasText(currentEsntlId)) {
            err.setResult("40");
            err.setMessage("로그인이 필요합니다.");
            return err;
        }
        if (!"MNR".equals(userSe != null ? userSe.trim() : "")) {
            err.setResult("40");
            err.setMessage("멘토 회원만 신청할 수 있습니다.");
            return err;
        }

        ArtpromUserDetailRequest promReq = new ArtpromUserDetailRequest();
        promReq.setProId(proId.trim());
        ArtpromUserDTO prom = artpromUserDAO.selectArtpromDetail(promReq);
        if (prom == null || !StringUtils.hasText(prom.getProGb())) {
            err.setResult("01");
            err.setMessage("지원사업을 찾을 수 없습니다.");
            return err;
        }
        String dbGb = prom.getProGb().trim();
        if (!"08".equals(dbGb) && !"09".equals(dbGb)) {
            err.setResult("01");
            err.setMessage("멘토 신청 대상 사업이 아닙니다.");
            return err;
        }
        String clientGb = request.getProGb() != null ? request.getProGb().trim() : "";
        if (!dbGb.equals(clientGb)) {
            err.setResult("01");
            err.setMessage("요청한 사업 구분이 공고와 일치하지 않습니다.");
            return err;
        }
        if ("08".equals(dbGb)) {
            if (!StringUtils.hasText(request.getReqSub()) || !StringUtils.hasText(request.getJoinTime())) {
                err.setResult("01");
                err.setMessage("희망과목과 희망 시간대는 필수입니다.");
                return err;
            }
        }

        ArtapmmMentorApplicationRegisterRequest inner = toMentorApplicationRegisterRequest(request, currentEsntlId.trim());
        return registerMentorApplication(proId, inner, chgUserId);
    }

    private static ArtapmmMentorApplicationRegisterRequest toMentorApplicationRegisterRequest(
            UserMentorApplicationRegisterRequest from,
            String reqEsntlId) {
        ArtapmmMentorApplicationRegisterRequest to = new ArtapmmMentorApplicationRegisterRequest();
        to.setReqEsntlId(reqEsntlId);
        to.setProSeq(from.getProSeq());
        to.setReqPlay(from.getReqPlay());
        to.setReqDesc(from.getReqDesc());
        to.setFileId(from.getFileId());
        to.setFileSeqs(from.getFileSeqs());
        to.setResultGb(from.getResultGb());
        to.setReqDt(from.getReqDt());
        to.setAprrDt(from.getAprrDt());
        to.setChgDt(from.getChgDt());
        to.setStopDt(from.getStopDt());
        to.setReaDesc(from.getReaDesc());
        to.setCollegeNm(from.getCollegeNm());
        to.setLeaveYn(from.getLeaveYn());
        to.setMajorNm(from.getMajorNm());
        to.setSchoolLvl(from.getSchoolLvl());
        to.setStudentId(from.getStudentId());
        to.setHschoolNm(from.getHschoolNm());
        to.setReqReason(from.getReqReason());
        to.setCareer(from.getCareer());
        to.setSttusCode(from.getSttusCode());
        to.setReqSub(from.getReqSub());
        to.setJoinTime(from.getJoinTime());
        to.setAgree1Yn(from.getAgree1Yn());
        to.setAgree2Yn(from.getAgree2Yn());
        to.setUniqId(from.getUniqId());
        return to;
    }

    private static String emptyToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        return s.trim();
    }

    private static String truncateToMax(String s, int max) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max);
    }

    /** Y/N 한 글자만 허용, 그 외·공백은 null */
    private static String normalizeYn(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        char c = s.trim().charAt(0);
        if (c == 'Y' || c == 'y') {
            return "Y";
        }
        if (c == 'N' || c == 'n') {
            return "N";
        }
        return null;
    }

    @Override
    public void updateArtappmSttusCodeByReqId(String reqId, String sttusCode, String reaDesc) throws Exception {
        if (reqId == null || reqId.trim().isEmpty() || sttusCode == null || sttusCode.trim().isEmpty()) {
            return;
        }
        artappmManageDAO.updateArtappmSttusCodeByReqId(reqId.trim(), sttusCode.trim(), reaDesc);
    }
}
