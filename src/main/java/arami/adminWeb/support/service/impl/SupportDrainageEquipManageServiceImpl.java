package arami.adminWeb.support.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arami.adminWeb.support.service.SupportDrainageEquipManageDAO;
import arami.adminWeb.support.service.SupportDrainageEquipManageService;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequmInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdPayStaSyncParam;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdPayStaUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequpSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipDetailRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipListRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentDetailSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipRegisterRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipUnpaidListRequest;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipExcelListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailRowResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentHistoryResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipRegisterResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipRegisterSkippedDetailResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipUnpaidListResponse;
import egovframework.com.cmm.EgovMessageSource;

@Slf4j
@Service("supportDrainageEquipManageService")
public class SupportDrainageEquipManageServiceImpl extends EgovAbstractServiceImpl
        implements SupportDrainageEquipManageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Resource(name = "supportDrainageEquipManageDAO")
    private SupportDrainageEquipManageDAO supportDrainageEquipManageDAO;

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Override
    @Transactional
    public SupportDrainageEquipRegisterResponse register(
            SupportDrainageEquipRegisterRequest request, String chgUserId) {
        SaveResult saveResult = saveDrainageEquip(request, chgUserId);
        String message = saveResult.isNewItem()
                ? egovMessageSource.getMessage("success.common.insert")
                : egovMessageSource.getMessage("success.common.update");
        return new SupportDrainageEquipRegisterResponse("00", message, saveResult.itemId());
    }

    @Override
    @Transactional
    public SupportDrainageEquipBasicUpdateResponse updateBasic(
            String itemId, SupportDrainageEquipBasicInfoRequest request, String chgUserId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        if (supportDrainageEquipManageDAO.countArtequmByItemId(id) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }
        SupportDrainageEquipArtequmInsertRequest row = buildBasicInfoRow(id, chgUserId, request);
        int updated = supportDrainageEquipManageDAO.updateArtequmBasic(row);
        if (updated <= 0) {
            throw new IllegalStateException("기본정보 수정에 실패했습니다.");
        }
        return new SupportDrainageEquipBasicUpdateResponse(
                "00",
                egovMessageSource.getMessage("success.common.update"),
                id);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportDrainageEquipListResponse selectDrainageEquipList(SupportDrainageEquipListRequest request) {
        SupportDrainageEquipListRequest queryParam = buildDrainageEquipListQueryParam(request);
        int totalCount = supportDrainageEquipManageDAO.selectDrainageEquipListCount(queryParam);
        SupportDrainageEquipListResponse response = new SupportDrainageEquipListResponse();
        response.setData(supportDrainageEquipManageDAO.selectDrainageEquipList(queryParam));
        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(totalCount);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportDrainageEquipUnpaidListResponse selectDrainageEquipUnpaidList(
            SupportDrainageEquipUnpaidListRequest request) {
        if (request == null || request.getBaseMonth() == null || request.getBaseMonth().isBlank()) {
            throw new IllegalArgumentException("기준월(baseMonth)은 필수입니다.");
        }
        String normalized;
        try {
            normalized = YearMonth.parse(request.getBaseMonth().trim()).toString();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("기준월 형식이 올바르지 않습니다.", ex);
        }
        SupportDrainageEquipUnpaidListRequest queryParam = new SupportDrainageEquipUnpaidListRequest();
        queryParam.setBaseMonth(normalized);
        queryParam.setStartIndex(request.getStartIndex());
        queryParam.setLengthPage(request.getLengthPage());
        queryParam.setStart(request.getStart());
        queryParam.setLength(request.getLength());
        applyPagingForSql(queryParam);

        int totalCount = supportDrainageEquipManageDAO.selectDrainageEquipUnpaidListCount(queryParam);
        SupportDrainageEquipUnpaidListResponse response = new SupportDrainageEquipUnpaidListResponse();
        response.setData(supportDrainageEquipManageDAO.selectDrainageEquipUnpaidList(queryParam));
        response.setRecordsTotal(totalCount);
        response.setRecordsFiltered(totalCount);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportDrainageEquipExcelListResponse selectDrainageEquipExcelList(
            SupportDrainageEquipListRequest request) {
        SupportDrainageEquipListRequest queryParam = buildDrainageEquipListSearchParam(request);
        SupportDrainageEquipExcelListResponse response = new SupportDrainageEquipExcelListResponse();
        response.setData(supportDrainageEquipManageDAO.selectDrainageEquipExcelList(queryParam));
        response.setResult("00");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportDrainageEquipDetailDataResponse selectDrainageEquipDetail(String itemId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        SupportDrainageEquipDetailDataResponse basic =
                supportDrainageEquipManageDAO.selectDrainageEquipBasicDetailByItemId(id);
        if (basic == null) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }
        List<SupportDrainageEquipDetailItemResponse> details =
                supportDrainageEquipManageDAO.selectDrainageEquipDetailListByItemId(id);
        basic.setDetails(details != null ? details : new ArrayList<>());
        return basic;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportDrainageEquipPaymentDetailDataResponse selectDrainageEquipPaymentDetail(String itemId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        SupportDrainageEquipPaymentDetailDataResponse basic =
                supportDrainageEquipManageDAO.selectDrainageEquipPaymentBasicDetailByItemId(id);
        if (basic == null) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        List<SupportDrainageEquipPaymentDetailRowResponse> rows =
                supportDrainageEquipManageDAO.selectDrainageEquipPaymentDetailRowsByItemId(id);

        Map<Integer, SupportDrainageEquipPaymentDetailItemResponse> detailMap = new LinkedHashMap<>();
        for (SupportDrainageEquipPaymentDetailRowResponse row : rows) {
            Integer seq = row.getSeq();
            if (seq == null) {
                continue;
            }
            SupportDrainageEquipPaymentDetailItemResponse detail = detailMap.computeIfAbsent(seq, k -> {
                SupportDrainageEquipPaymentDetailItemResponse d =
                        new SupportDrainageEquipPaymentDetailItemResponse();
                d.setSeq(row.getSeq());
                d.setPaySta(row.getPaySta());
                d.setReqDate(row.getReqDate());
                d.setStartDate(row.getStartDate());
                d.setPlanDate(row.getPlanDate());
                d.setCompDate(row.getCompDate());
                d.setAgency(row.getAgency());
                d.setEquipCost(row.getEquipCost());
                d.setEquipPay(row.getEquipPay());
                d.setPayments(new ArrayList<>());
                return d;
            });
            if (row.getSeq2() != null) {
                detail.getPayments().add(new SupportDrainageEquipPaymentHistoryResponse(
                        row.getSeq2(),
                        row.getPayDay(),
                        row.getPay(),
                        row.getPayDesc()));
            }
        }
        basic.setDetails(new ArrayList<>(detailMap.values()));
        return basic;
    }

    @Override
    @Transactional
    public SupportDrainageEquipPaymentSaveResponse saveDrainageEquipPayments(
            SupportDrainageEquipPaymentSaveRequest request, String chgUserId) {
        String itemId = trimToEmpty(request.getItemId());
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        if (supportDrainageEquipManageDAO.countArtequmByItemId(itemId) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        Set<Integer> existingDetailSeqSet =
                new HashSet<>(supportDrainageEquipManageDAO.selectArtequdSeqsByItemId(itemId));
        List<SupportDrainageEquipRegisterSkippedDetailResponse> skippedDetails = new ArrayList<>();

        for (SupportDrainageEquipPaymentDetailSaveRequest detail : request.getDetails()) {
            Integer seq = detail.getSeq();
            validateExistingSeq(seq, existingDetailSeqSet, "납부저장");

            if (!isStoredDetailUnpaid(itemId, seq)) {
                log.warn(
                        "완납 처리된 분은 납부내역 저장 생략(동일 요청 내 다른 SEQ 계속 처리). itemId={}, seq={}",
                        itemId,
                        seq);
                if (trimToNull(detail.getPaySta()) != null) {
                    log.warn(
                            "완납 처리된 분은 PAY_STA 변경 생략(동일 요청 내 다른 SEQ 계속 처리). itemId={}, seq={}",
                            itemId,
                            seq);
                }
                skippedDetails.add(new SupportDrainageEquipRegisterSkippedDetailResponse(
                        seq,
                        "PAYMENT",
                        SupportDrainageEquipRegisterSkippedDetailResponse.SKIP_REASON_PAID,
                        null));
                continue;
            }

            List<Integer> existingPaySeq2s =
                    supportDrainageEquipManageDAO.selectArtequpSeq2sByItemIdAndSeq(itemId, seq);
            Set<Integer> existingPaySeq2Set = new HashSet<>(existingPaySeq2s);
            Integer nextSeq2Hint = supportDrainageEquipManageDAO.getNextArtequpSeq2(itemId, seq);
            int nextSeq2 = nextSeq2Hint != null ? nextSeq2Hint : 1;

            List<SupportDrainageEquipPaymentRequest> payments = detail.getPayments() != null
                    ? detail.getPayments()
                    : List.of();
            for (SupportDrainageEquipPaymentRequest payment : payments) {
                String rawRowStatus = trimToNull(payment.getRowStatus());
                if (rawRowStatus == null) {
                    continue;
                }
                String rowStatus = rawRowStatus.toUpperCase();
                Integer requestSeq2 = payment.getSeq2();

                if ("D".equals(rowStatus)) {
                    if (!canProcessExistingPaySeq2(requestSeq2, existingPaySeq2Set)) {
                        log.warn(
                                "삭제 대상 납부내역 SEQ2가 존재하지 않아 해당 행을 건너뜁니다. itemId={}, seq={}, seq2={}",
                                itemId,
                                seq,
                                requestSeq2);
                        continue;
                    }
                    supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeqAndSeq2(itemId, seq, requestSeq2);
                    existingPaySeq2Set.remove(requestSeq2);
                    continue;
                }

                if ("U".equals(rowStatus)) {
                    if (!canProcessExistingPaySeq2(requestSeq2, existingPaySeq2Set)) {
                        log.warn(
                                "수정 대상 납부내역 SEQ2가 존재하지 않아 해당 행을 건너뜁니다. itemId={}, seq={}, seq2={}",
                                itemId,
                                seq,
                                requestSeq2);
                        continue;
                    }
                    SupportDrainageEquipArtequpSaveRequest updateRow = new SupportDrainageEquipArtequpSaveRequest();
                    updateRow.setItemId(itemId);
                    updateRow.setSeq(seq);
                    updateRow.setSeq2(requestSeq2);
                    updateRow.setPayDay(parsePaymentDate(payment.getPayDay()));
                    updateRow.setPay(zeroIfNull(payment.getPay()));
                    updateRow.setPayDesc(trimToNull(payment.getPayDesc()));
                    updateRow.setChgUserId(chgUserId);
                    int updated = supportDrainageEquipManageDAO.updateArtequp(updateRow);
                    if (updated <= 0) {
                        throw new IllegalStateException(
                                "납부내역 수정에 실패했습니다. itemId=" + itemId + ", seq=" + seq + ", seq2=" + requestSeq2);
                    }
                    continue;
                }

                if ("I".equals(rowStatus)) {
                    if (requestSeq2 != null && requestSeq2 > 0) {
                        throw new IllegalArgumentException("신규 납부내역(I)에는 seq2를 지정할 수 없습니다.");
                    }
                    SupportDrainageEquipArtequpSaveRequest row = new SupportDrainageEquipArtequpSaveRequest();
                    row.setItemId(itemId);
                    row.setSeq(seq);
                    row.setPayDay(parsePaymentDate(payment.getPayDay()));
                    row.setPay(zeroIfNull(payment.getPay()));
                    row.setPayDesc(trimToNull(payment.getPayDesc()));
                    row.setChgUserId(chgUserId);
                    row.setSeq2(nextSeq2++);
                    supportDrainageEquipManageDAO.insertArtequp(row);
                    continue;
                }

                throw new IllegalArgumentException("payment.rowStatus 값이 올바르지 않습니다. (I, U, D)");
            }

            String newPaySta = trimToNull(detail.getPaySta());
            if (newPaySta != null) {
                SupportDrainageEquipArtequdPayStaUpdateRequest staRow =
                        new SupportDrainageEquipArtequdPayStaUpdateRequest();
                staRow.setItemId(itemId);
                staRow.setSeq(seq);
                staRow.setPaySta(newPaySta);
                staRow.setChgUserId(chgUserId);
                int staUpdated = supportDrainageEquipManageDAO.updateArtequdPaySta(staRow);
                if (staUpdated <= 0) {
                    throw new IllegalStateException("납부 상태 수정에 실패했습니다. seq=" + seq);
                }
            }
        }

        supportDrainageEquipManageDAO.updateArtequdEquipPayByItemId(itemId);
        supportDrainageEquipManageDAO.updateArtequdPayStaByEquipPayVsCost(
                new SupportDrainageEquipArtequdPayStaSyncParam(itemId, chgUserId));

        return new SupportDrainageEquipPaymentSaveResponse(
                "00",
                egovMessageSource.getMessage("success.common.insert"),
                itemId,
                skippedDetails);
    }

    @Override
    @Transactional
    public SupportDrainageEquipDeleteResponse deleteDrainageEquipPayment(
            SupportDrainageEquipPaymentDeleteRequest request, String chgUserId) {
        String itemId = trimToEmpty(request.getItemId());
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        Integer seqObj = request.getSeq();
        if (seqObj == null || seqObj <= 0) {
            throw new IllegalArgumentException("SEQ는 필수입니다.");
        }
        int seq = seqObj;
        Integer seq2Obj = request.getSeq2();
        if (seq2Obj == null || seq2Obj <= 0) {
            throw new IllegalArgumentException("납부내역 SEQ2는 필수입니다.");
        }
        int seq2 = seq2Obj;
        if (supportDrainageEquipManageDAO.countArtequmByItemId(itemId) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }
        Set<Integer> existingDetailSeqSet =
                new HashSet<>(supportDrainageEquipManageDAO.selectArtequdSeqsByItemId(itemId));
        validateExistingSeq(seq, existingDetailSeqSet, "납부내역삭제");
        if (!isStoredDetailUnpaid(itemId, seq)) {
            throw new IllegalArgumentException("완납 건의 납부내역은 삭제하실 수 없습니다.");
        }
        List<Integer> existingPaySeq2s =
                supportDrainageEquipManageDAO.selectArtequpSeq2sByItemIdAndSeq(itemId, seq);
        Set<Integer> existingPaySeq2Set = new HashSet<>(existingPaySeq2s);
        if (!canProcessExistingPaySeq2(seq2, existingPaySeq2Set)) {
            throw new IllegalArgumentException("삭제 대상 납부내역이 존재하지 않습니다.");
        }
        int deleted = supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeqAndSeq2(itemId, seq, seq2);
        if (deleted <= 0) {
            throw new IllegalStateException("납부내역 삭제에 실패했습니다.");
        }
        supportDrainageEquipManageDAO.updateArtequdEquipPayByItemId(itemId);
        supportDrainageEquipManageDAO.updateArtequdPayStaByEquipPayVsCost(
                new SupportDrainageEquipArtequdPayStaSyncParam(itemId, chgUserId));
        return new SupportDrainageEquipDeleteResponse(
                "00",
                egovMessageSource.getMessage("success.common.delete"),
                itemId,
                seq);
    }

    @Override
    @Transactional
    public SupportDrainageEquipDeleteResponse deleteDrainageEquipDetail(
            SupportDrainageEquipDeleteRequest request, String chgUserId) {
        String itemId = trimToEmpty(request.getItemId());
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        Integer seq = request.getSeq();
        if (seq == null || seq <= 0) {
            throw new IllegalArgumentException("SEQ는 필수입니다.");
        }
        if (supportDrainageEquipManageDAO.countArtequmByItemId(itemId) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        Set<Integer> existingSeqSet =
                new HashSet<>(supportDrainageEquipManageDAO.selectArtequdSeqsByItemId(itemId));
        validateExistingSeq(seq, existingSeqSet, "삭제");

        String paySta = trimToEmpty(supportDrainageEquipManageDAO.selectArtequdPayStaByItemIdAndSeq(itemId, seq));
        if ("02".equals(paySta)) {
            throw new IllegalArgumentException("완납 건은 삭제하실 수 없습니다.");
        }
        if (!"01".equals(paySta)) {
            throw new IllegalArgumentException("미납 건만 삭제할 수 있습니다.");
        }

        boolean lastArtequdForItem =
                supportDrainageEquipManageDAO.selectArtequdSeqsByItemId(itemId).size() == 1;

        supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeq(itemId, seq);
        int deletedArtequd = supportDrainageEquipManageDAO.deleteArtequdByItemIdAndSeq(itemId, seq);
        if (deletedArtequd <= 0) {
            throw new IllegalStateException("삭제 대상이 존재하지 않습니다.");
        }

        if (lastArtequdForItem) {
            int deletedArtequm = supportDrainageEquipManageDAO.deleteArtequmByItemId(itemId);
            if (deletedArtequm <= 0) {
                throw new IllegalStateException("ARTEQUM 삭제에 실패했습니다.");
            }
        }

        return new SupportDrainageEquipDeleteResponse(
                "00",
                egovMessageSource.getMessage("success.common.delete"),
                itemId,
                seq);
    }

    private SaveResult saveDrainageEquip(SupportDrainageEquipRegisterRequest request, String chgUserId) {
        String itemId;
        boolean isNewItem = trimToEmpty(request.getItemId()).isEmpty();

        if (isNewItem) {
            itemId = supportDrainageEquipManageDAO.getNextItemId();
            if (itemId == null || itemId.isBlank()) {
                throw new IllegalStateException("ITEM_ID 채번에 실패했습니다.");
            }
            SupportDrainageEquipArtequmInsertRequest basic = buildBasicInfo(itemId, chgUserId, request);
            supportDrainageEquipManageDAO.insertArtequm(basic);
        } else {
            itemId = trimToEmpty(request.getItemId());
            if (supportDrainageEquipManageDAO.countArtequmByItemId(itemId) <= 0) {
                throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
            }
            SupportDrainageEquipArtequmInsertRequest basic =
                    buildBasicInfoRow(itemId, chgUserId, request.getBasicInfo());
            int updated = supportDrainageEquipManageDAO.updateArtequmBasic(basic);
            if (updated <= 0) {
                throw new IllegalStateException("기본정보 수정에 실패했습니다.");
            }
        }

        List<SupportDrainageEquipDetailRequest> details = request.getDetails();
        validateExplicitSeqUnique(details);

        List<Integer> existingSeqs = isNewItem
                ? List.of()
                : supportDrainageEquipManageDAO.selectArtequdSeqsByItemId(itemId);
        Set<Integer> existingSeqSet = new HashSet<>(existingSeqs);

        Integer nextSeqHint = supportDrainageEquipManageDAO.getNextArtequdSeq(itemId);
        int nextNewSeq = nextSeqHint != null ? nextSeqHint : 1;

        for (SupportDrainageEquipDetailRequest detail : details) {
            String rawRowStatus = trimToNull(detail.getRowStatus());
            if (rawRowStatus == null) {
                continue;
            }
            String rowStatus = rawRowStatus.toUpperCase();
            Integer requestSeq = detail.getSeq();

            if ("D".equals(rowStatus)) {
                if (isNewItem) {
                    throw new IllegalArgumentException("신규 등록에서는 삭제를 사용할 수 없습니다.");
                }
                validateExistingSeq(requestSeq, existingSeqSet, "삭제");
                deleteDetailBlock(itemId, requestSeq);
                existingSeqSet.remove(requestSeq);
                continue;
            }

            if ("U".equals(rowStatus)) {
                if (isNewItem) {
                    throw new IllegalArgumentException("신규 등록에서는 수정을 사용할 수 없습니다.");
                }
                validateExistingSeq(requestSeq, existingSeqSet, "수정");
                upsertDetailBlock(itemId, chgUserId, detail, requestSeq);
                continue;
            }

            if ("I".equals(rowStatus)) {
                if (!isNewItem && supportDrainageEquipManageDAO.countUnpaidArtequdByItemId(itemId) > 0) {
                    throw new IllegalArgumentException(
                            "미납건이 존재하여 추가 등록을 할 수 없습니다. 모든 건을 완납처리 후 진행해주세요.");
                }
                int seq = nextNewSeq++;
                upsertDetailBlock(itemId, chgUserId, detail, seq);
                existingSeqSet.add(seq);
                continue;
            }

            throw new IllegalArgumentException("등록 상태 값이 올바르지 않습니다. (I/U/D, blank rowStatus skips row)");
        }

        return new SaveResult(itemId, isNewItem);
    }

    private SupportDrainageEquipArtequmInsertRequest buildBasicInfo(
            String itemId,
            String chgUserId,
            SupportDrainageEquipRegisterRequest request) {
        return buildBasicInfoRow(itemId, chgUserId, request.getBasicInfo());
    }

    private SupportDrainageEquipArtequmInsertRequest buildBasicInfoRow(
            String itemId,
            String chgUserId,
            SupportDrainageEquipBasicInfoRequest basicInfo) {
        SupportDrainageEquipArtequmInsertRequest basic = new SupportDrainageEquipArtequmInsertRequest();
        basic.setItemId(itemId);
        basic.setUserNm(trimToEmpty(basicInfo.getUserNm()));
        basic.setZip(trimToEmpty(basicInfo.getZip()));
        basic.setAdresLot(trimToEmpty(basicInfo.getAdresLot()));
        basic.setAdres(trimToEmpty(basicInfo.getAdres()));
        basic.setDetailAdres(trimToEmpty(basicInfo.getDetailAdres()));
        basic.setUsrTelno(trimToEmpty(basicInfo.getUsrTelno()));
        basic.setChgUserId(chgUserId);
        return basic;
    }

    private void upsertDetailBlock(
            String itemId,
            String chgUserId,
            SupportDrainageEquipDetailRequest detail,
            int seq) {
        SupportDrainageEquipArtequdInsertRequest row = new SupportDrainageEquipArtequdInsertRequest();
        row.setItemId(itemId);
        row.setSeq(seq);
        row.setPaySta(trimToNull(detail.getPaySta()));
        row.setReqDate(parseDate(detail.getReqDate(), "등록일"));
        row.setStartDate(parseDate(detail.getStartDate(), "착공일"));
        row.setPlanDate(parseDate(detail.getPlanDate(), "준공예정일"));
        row.setCompDate(parseDate(detail.getCompDate(), "준공일"));
        row.setAgency(trimToNull(detail.getAgency()));
        row.setEquipCost(zeroIfNull(detail.getEquipCost()));
        row.setEquipPay(zeroIfNull(detail.getEquipPay()));
        row.setChgUserId(chgUserId);
        supportDrainageEquipManageDAO.upsertArtequd(row);
    }

    private void deleteDetailBlock(String itemId, int seq) {
        supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeq(itemId, seq);
        supportDrainageEquipManageDAO.deleteArtequdByItemIdAndSeq(itemId, seq);
    }

    private static void validateExplicitSeqUnique(List<SupportDrainageEquipDetailRequest> details) {
        Set<Integer> seen = new HashSet<>();
        for (SupportDrainageEquipDetailRequest d : details) {
            if (d.getSeq() != null && d.getSeq() > 0) {
                if (!seen.add(d.getSeq())) {
                    throw new IllegalArgumentException("요청에 중복된 SEQ가 있습니다.");
                }
            }
        }
    }

    private static void validateExistingSeq(Integer seq, Set<Integer> existingSeqSet, String actionName) {
        if (seq == null || seq <= 0) {
            throw new IllegalArgumentException(actionName + " 대상 detail.seq는 필수입니다.");
        }
        if (!existingSeqSet.contains(seq)) {
            throw new IllegalArgumentException(actionName + " 대상 SEQ가 존재하지 않습니다. seq=" + seq);
        }
    }

    private boolean isStoredDetailUnpaid(String itemId, int seq) {
        String paySta = trimToEmpty(supportDrainageEquipManageDAO.selectArtequdPayStaByItemIdAndSeq(itemId, seq));
        return "01".equals(paySta);
    }

    private static boolean canProcessExistingPaySeq2(Integer seq2, Set<Integer> existingSeq2Set) {
        return seq2 != null && seq2 > 0 && existingSeq2Set.contains(seq2);
    }

    private static SupportDrainageEquipListRequest buildDrainageEquipListQueryParam(
            SupportDrainageEquipListRequest request) {
        SupportDrainageEquipListRequest queryParam = buildDrainageEquipListSearchParam(request);
        queryParam.setStartIndex(request != null ? request.getStartIndex() : null);
        queryParam.setLengthPage(request != null ? request.getLengthPage() : null);
        queryParam.setStart(request != null ? request.getStart() : null);
        queryParam.setLength(request != null ? request.getLength() : null);
        applyPagingForSql(queryParam);
        return queryParam;
    }

    private static SupportDrainageEquipListRequest buildDrainageEquipListSearchParam(
            SupportDrainageEquipListRequest request) {
        SupportDrainageEquipListRequest src = request != null ? request : new SupportDrainageEquipListRequest();
        SupportDrainageEquipListRequest queryParam = new SupportDrainageEquipListRequest();
        queryParam.setReqDateFrom(src.getReqDateFrom());
        queryParam.setReqDateTo(src.getReqDateTo());
        queryParam.setUserNm(src.getUserNm());
        queryParam.setAddress(src.getAddress());
        queryParam.setPaySta(normalizeListPayStaFilter(src.getPaySta()));
        return queryParam;
    }

    private static String normalizeListPayStaFilter(String paySta) {
        if (paySta == null) {
            return null;
        }
        String v = paySta.trim();
        if (v.isEmpty() || "00".equals(v)) {
            return null;
        }
        if ("01".equals(v) || "02".equals(v)) {
            return v;
        }
        throw new IllegalArgumentException("납부상태(paySta)는 00(전체), 01(미납), 02(완납)만 가능합니다.");
    }

    private static void applyPagingForSql(SupportDrainageEquipListRequest request) {
        request.setDefaultPaging();
        request.setStartIndex(sanitizePagingStart(request.getStartIndex()));
        request.setLengthPage(sanitizePagingLength(request.getLengthPage()));
    }

    private static void applyPagingForSql(SupportDrainageEquipUnpaidListRequest request) {
        request.setDefaultPaging();
        request.setStartIndex(sanitizePagingStart(request.getStartIndex()));
        request.setLengthPage(sanitizePagingLength(request.getLengthPage()));
    }

    private static int sanitizePagingStart(Integer startIndex) {
        if (startIndex == null || startIndex < 0) {
            return 0;
        }
        return startIndex;
    }

    private static int sanitizePagingLength(Integer lengthPage) {
        if (lengthPage == null || lengthPage <= 0) {
            return 15;
        }
        return Math.min(lengthPage, 500);
    }

    private static Date parsePaymentDate(String raw) {
        String value = trimToNull(raw);
        if (value == null) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(value, DATE_FORMATTER);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("납부일 형식이 올바르지 않습니다. (yyyy-MM-dd)");
        }
    }

    private record SaveResult(String itemId, boolean isNewItem) {
    }

    private static Integer zeroIfNull(Integer n) {
        return n == null ? 0 : n;
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Date parseDate(String raw, String fieldLabel) {
        String value = trimToNull(raw);
        if (value == null) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(value, DATE_FORMATTER);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    fieldLabel + " 형식이 올바르지 않습니다. (yyyy-MM-dd)");
        }
    }
}
