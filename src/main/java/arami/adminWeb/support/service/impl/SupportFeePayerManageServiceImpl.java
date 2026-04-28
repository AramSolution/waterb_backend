package arami.adminWeb.support.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arami.adminWeb.support.service.SupportFeePayerManageDAO;
import arami.adminWeb.support.service.SupportFeePayerManageService;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitemInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitecInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitepSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedPayStaUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedCostUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerCalcRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerCostCalcRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerDetailRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentDetailSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerRegisterRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerCalculateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailCalculationResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailRowResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentHistoryResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterSkippedDetailResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterResponse;
import egovframework.com.cmm.EgovMessageSource;

@Slf4j
@Service("supportFeePayerManageService")
public class SupportFeePayerManageServiceImpl extends EgovAbstractServiceImpl implements SupportFeePayerManageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Resource(name = "supportFeePayerManageDAO")
    private SupportFeePayerManageDAO supportFeePayerManageDAO;

    @Resource(name = "egovMessageSource")
    private EgovMessageSource egovMessageSource;

    @Override
    @Transactional
    public SupportFeePayerRegisterResponse register(SupportFeePayerRegisterRequest request, String chgUserId) {
        SaveResult saveResult = saveFeePayer(request, true, chgUserId);
        String message = saveResult.isNewItem()
                ? egovMessageSource.getMessage("success.common.insert")
                : egovMessageSource.getMessage("success.common.update");
        return new SupportFeePayerRegisterResponse("00", message, saveResult.itemId(), saveResult.skippedDetails());
    }

    @Override
    @Transactional
    public SupportFeePayerCalculateResponse calculateCost(SupportFeePayerRegisterRequest request, String chgUserId) {
        SaveResult saveResult = saveFeePayer(request, true, chgUserId);
        if (saveResult.targetSeqForCalculate() == null || saveResult.targetSeqForCalculate() <= 0) {
            throw new IllegalArgumentException("계산 대상 detail이 없습니다. (I/U rowStatus 필요)");
        }
        CostValues values = calculateAndUpdateCost(saveResult.itemId(), saveResult.targetSeqForCalculate(), chgUserId);
        return new SupportFeePayerCalculateResponse(
                "00",
                egovMessageSource.getMessage("success.common.select"),
                saveResult.itemId(),
                saveResult.targetSeqForCalculate(),
                values.waterCost(),
                values.waterVal(),
                values.waterSum(),
                saveResult.skippedDetails());
    }

    private SaveResult saveFeePayer(SupportFeePayerRegisterRequest request, boolean runCostCalculationOnSave, String chgUserId) {
        String itemId;
        boolean isNewItem = trimToEmpty(request.getItemId()).isEmpty();

        if (isNewItem) {
            itemId = supportFeePayerManageDAO.getNextItemId();
            if (itemId == null || itemId.isBlank()) {
                throw new IllegalStateException("ITEM_ID 채번에 실패했습니다.");
            }
            SupportFeePayerArtitemInsertRequest basic = buildBasicInfo(itemId, chgUserId, request);
            supportFeePayerManageDAO.insertArtitem(basic);
        } else {
            itemId = trimToEmpty(request.getItemId());
            if (supportFeePayerManageDAO.countArtitemByItemId(itemId) <= 0) {
                throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
            }
            SupportFeePayerArtitemInsertRequest basic = buildBasicInfoRow(itemId, chgUserId, request.getBasicInfo());
            int updated = supportFeePayerManageDAO.updateArtitemBasic(basic);
            if (updated <= 0) {
                throw new IllegalStateException("기본정보 수정에 실패했습니다.");
            }
        }

        List<SupportFeePayerDetailRequest> details = request.getDetails();
        validateExplicitSeqUnique(details);

        List<Integer> existingSeqs = isNewItem
                ? List.of()
                : supportFeePayerManageDAO.selectArtitedSeqsByItemId(itemId);
        Set<Integer> existingSeqSet = new HashSet<>(existingSeqs);
        Integer targetSeqForCalculate = null;

        Integer nextSeqHint = supportFeePayerManageDAO.getNextArtitedSeq(itemId);
        int nextNewSeq = nextSeqHint != null ? nextSeqHint : 1;

        List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails = new ArrayList<>();

        for (SupportFeePayerDetailRequest detail : details) {
            String rowStatus = resolveRowStatus(detail);
            Integer requestSeq = detail.getSeq();

            if ("D".equals(rowStatus)) {
                if (isNewItem) {
                    throw new IllegalArgumentException("신규 등록에서는 detail 삭제(D)를 사용할 수 없습니다.");
                }
                validateExistingSeq(requestSeq, existingSeqSet, "삭제");
                if (!isStoredDetailUnpaid(itemId, requestSeq)) {
                    log.warn(
                            "완납 처리된 건은 삭제 생략(동일 요청 내 다른 건 계속 처리). itemId={}, seq={}",
                            itemId,
                            requestSeq);
                    skippedDetails.add(new SupportFeePayerRegisterSkippedDetailResponse(
                            requestSeq,
                            "D",
                            SupportFeePayerRegisterSkippedDetailResponse.SKIP_REASON_PAID,
                            null));
                    continue;
                }
                deleteDetailBlock(itemId, requestSeq);
                existingSeqSet.remove(requestSeq);
                continue;
            }

            if ("U".equals(rowStatus)) {
                if (isNewItem) {
                    throw new IllegalArgumentException("신규 등록에서는 detail 수정(U)을 사용할 수 없습니다.");
                }
                validateExistingSeq(requestSeq, existingSeqSet, "수정");
                if (!isStoredDetailUnpaid(itemId, requestSeq)) {
                    log.warn(
                            "완납 처리된 건은 수정 생략(동일 요청 내 다른 건 계속 처리). itemId={}, seq={}",
                            itemId,
                            requestSeq);
                    skippedDetails.add(new SupportFeePayerRegisterSkippedDetailResponse(
                            requestSeq,
                            "U",
                            SupportFeePayerRegisterSkippedDetailResponse.SKIP_REASON_PAID,
                            null));
                    continue;
                }
                upsertDetailBlock(itemId, chgUserId, detail, requestSeq, runCostCalculationOnSave);
                targetSeqForCalculate = requestSeq;
                continue;
            }

            if ("I".equals(rowStatus)) {
                if (requestSeq != null && requestSeq > 0) {
                    throw new IllegalArgumentException("신규 detail(I)에는 seq를 지정할 수 없습니다.");
                }
                if (!isNewItem && supportFeePayerManageDAO.countUnpaidArtitedByItemId(itemId) > 0) {
                    throw new IllegalArgumentException(
                            "미납건이 존재하여 추가 등록을 할 수 없습니다. 모든 건을 완납처리 후 진행해주세요.");
                }
                int seq = nextNewSeq++;
                upsertDetailBlock(itemId, chgUserId, detail, seq, runCostCalculationOnSave);
                existingSeqSet.add(seq);
                targetSeqForCalculate = seq;
                continue;
            }

            throw new IllegalArgumentException("detail.rowStatus 값이 올바르지 않습니다. (I/U/D)");
        }
        return new SaveResult(itemId, isNewItem, targetSeqForCalculate, skippedDetails);
    }

    @Override
    @Transactional
    public SupportFeePayerBasicUpdateResponse updateBasic(String itemId, SupportFeePayerBasicInfoRequest request, String chgUserId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        if (supportFeePayerManageDAO.countArtitemByItemId(id) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }
        SupportFeePayerArtitemInsertRequest row = buildBasicInfoRow(id, chgUserId, request);
        int updated = supportFeePayerManageDAO.updateArtitemBasic(row);
        if (updated <= 0) {
            throw new IllegalStateException("기본정보 수정에 실패했습니다.");
        }
        return new SupportFeePayerBasicUpdateResponse(
                "00",
                egovMessageSource.getMessage("success.common.update"),
                id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportFeePayerListItemResponse> selectFeePayerList(SupportFeePayerListRequest request) {
        return supportFeePayerManageDAO.selectFeePayerList(request);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportFeePayerDetailDataResponse selectFeePayerDetail(String itemId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        SupportFeePayerDetailDataResponse basic = supportFeePayerManageDAO.selectFeePayerBasicDetailByItemId(id);
        if (basic == null) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        List<SupportFeePayerDetailItemResponse> details = supportFeePayerManageDAO.selectFeePayerDetailListByItemId(id);
        List<SupportFeePayerDetailCalculationResponse> calculations = supportFeePayerManageDAO
                .selectFeePayerCalculationListByItemId(id);

        Map<Integer, List<SupportFeePayerDetailCalculationResponse>> calcBySeq = new HashMap<>();
        for (SupportFeePayerDetailCalculationResponse calc : calculations) {
            Integer seq = calc.getSeq();
            if (seq == null) {
                continue;
            }
            calcBySeq.computeIfAbsent(seq, k -> new ArrayList<>()).add(calc);
        }

        for (SupportFeePayerDetailItemResponse detail : details) {
            List<SupportFeePayerDetailCalculationResponse> rows = calcBySeq.get(detail.getSeq());
            detail.setCalculations(rows != null ? rows : new ArrayList<>());
        }

        basic.setDetails(details);
        return basic;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportFeePayerPaymentDetailDataResponse selectFeePayerPaymentDetail(String itemId) {
        String id = trimToEmpty(itemId);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }

        SupportFeePayerPaymentDetailDataResponse basic = supportFeePayerManageDAO
                .selectFeePayerPaymentBasicDetailByItemId(id);
        if (basic == null) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        List<SupportFeePayerPaymentDetailRowResponse> rows = supportFeePayerManageDAO
                .selectFeePayerPaymentDetailRowsByItemId(id);

        Map<Integer, SupportFeePayerPaymentDetailItemResponse> detailMap = new LinkedHashMap<>();
        for (SupportFeePayerPaymentDetailRowResponse row : rows) {
            Integer seq = row.getSeq();
            if (seq == null) {
                continue;
            }

            SupportFeePayerPaymentDetailItemResponse detail = detailMap.computeIfAbsent(seq, k -> {
                SupportFeePayerPaymentDetailItemResponse d = new SupportFeePayerPaymentDetailItemResponse();
                d.setSeq(row.getSeq());
                d.setPaySta(row.getPaySta());
                d.setType1(row.getType1());
                d.setType2(row.getType2());
                d.setReqDate(row.getReqDate());
                d.setBaseCost(row.getBaseCost());
                d.setWaterSum(row.getWaterSum());
                d.setWaterVal(row.getWaterVal());
                d.setWaterCost(row.getWaterCost());
                d.setWaterPay(row.getWaterPay());
                d.setPayments(new ArrayList<>());
                return d;
            });

            if (row.getSeq2() != null) {
                detail.getPayments().add(new SupportFeePayerPaymentHistoryResponse(
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
    public SupportFeePayerPaymentSaveResponse saveFeePayerPayments(SupportFeePayerPaymentSaveRequest request, String chgUserId) {
        String itemId = trimToEmpty(request.getItemId());
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }
        if (supportFeePayerManageDAO.countArtitemByItemId(itemId) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        Set<Integer> existingDetailSeqSet = new HashSet<>(supportFeePayerManageDAO.selectArtitedSeqsByItemId(itemId));

        List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails = new ArrayList<>();

        for (SupportFeePayerPaymentDetailSaveRequest detail : request.getDetails()) {
            Integer seq = detail.getSeq();
            validateExistingSeq(seq, existingDetailSeqSet, "납부저장");

            // 등록 API와 동일: DB ARTITED.PAY_STA 기준 — 미납('01')만 납부·상태 갱신 허용. 완납('02') 등은 해당 SEQ만 생략.
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
                skippedDetails.add(new SupportFeePayerRegisterSkippedDetailResponse(
                        seq,
                        "PAYMENT",
                        SupportFeePayerRegisterSkippedDetailResponse.SKIP_REASON_PAID,
                        null));
                continue;
            }

            List<Integer> existingPaySeq2s = supportFeePayerManageDAO.selectArtitepSeq2sByItemIdAndSeq(itemId, seq);
            Set<Integer> existingPaySeq2Set = new HashSet<>(existingPaySeq2s);
            Integer nextSeq2Hint = supportFeePayerManageDAO.getNextArtitepSeq2(itemId, seq);
            int nextSeq2 = nextSeq2Hint != null ? nextSeq2Hint : 1;

            List<SupportFeePayerPaymentRequest> payments = detail.getPayments() != null
                    ? detail.getPayments()
                    : List.of();
            for (SupportFeePayerPaymentRequest payment : payments) {
                String rowStatus = resolvePaymentRowStatus(payment);
                Integer requestSeq2 = payment.getSeq2();

                if ("D".equals(rowStatus)) {
                    validateExistingPaySeq2(requestSeq2, existingPaySeq2Set, "삭제");
                    supportFeePayerManageDAO.deleteArtitepByItemIdAndSeqAndSeq2(itemId, seq, requestSeq2);
                    existingPaySeq2Set.remove(requestSeq2);
                    continue;
                }

                if ("U".equals(rowStatus)) {
                    validateExistingPaySeq2(requestSeq2, existingPaySeq2Set, "수정");
                    log.warn(
                            "납부내역 수정(U)은 허용되지 않습니다. 요청만 수신하고 갱신하지 않습니다. itemId={}, seq={}, seq2={}",
                            itemId,
                            seq,
                            requestSeq2);
                    skippedDetails.add(new SupportFeePayerRegisterSkippedDetailResponse(
                            seq,
                            "U",
                            SupportFeePayerRegisterSkippedDetailResponse.SKIP_REASON_UPDATE_NOT_ALLOWED,
                            requestSeq2));
                    continue;
                }

                if ("I".equals(rowStatus)) {
                    if (requestSeq2 != null && requestSeq2 > 0) {
                        throw new IllegalArgumentException("신규 납부내역(I)에는 seq2를 지정할 수 없습니다.");
                    }
                    SupportFeePayerArtitepSaveRequest row = new SupportFeePayerArtitepSaveRequest();
                    row.setItemId(itemId);
                    row.setSeq(seq);
                    row.setPayDay(parsePaymentDate(payment.getPayDay()));
                    row.setPay(zeroIfNull(payment.getPay()));
                    row.setPayDesc(trimToNull(payment.getPayDesc()));
                    row.setChgUserId(chgUserId);
                    row.setSeq2(nextSeq2++);
                    supportFeePayerManageDAO.insertArtitep(row);
                    continue;
                }

                throw new IllegalArgumentException("payment.rowStatus 값이 올바르지 않습니다. (I/D, U는 갱신되지 않음)");
            }

            String newPaySta = trimToNull(detail.getPaySta());
            if (newPaySta != null) {
                SupportFeePayerArtitedPayStaUpdateRequest staRow = new SupportFeePayerArtitedPayStaUpdateRequest();
                staRow.setItemId(itemId);
                staRow.setSeq(seq);
                staRow.setPaySta(newPaySta);
                staRow.setChgUserId(chgUserId);
                int staUpdated = supportFeePayerManageDAO.updateArtitedPaySta(staRow);
                if (staUpdated <= 0) {
                    throw new IllegalStateException("납부 상태(PAY_STA) 수정에 실패했습니다. seq=" + seq);
                }
            }
        }

        // 납부내역 저장 완료 후 ITEM_ID의 각 SEQ별 누적 납부금액을 ARTITED.WATER_PAY에 반영
        supportFeePayerManageDAO.updateArtitedWaterPayByItemId(itemId);

        return new SupportFeePayerPaymentSaveResponse(
                "00",
                egovMessageSource.getMessage("success.common.insert"),
                itemId,
                skippedDetails);
    }

    @Override
    @Transactional
    public SupportFeePayerDeleteResponse deleteFeePayerDetail(SupportFeePayerDeleteRequest request) {
        String itemId = trimToEmpty(request.getItemId());
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("ITEM_ID는 필수입니다.");
        }

        Integer seq = request.getSeq();
        if (seq == null || seq <= 0) {
            throw new IllegalArgumentException("SEQ는 필수입니다.");
        }

        if (supportFeePayerManageDAO.countArtitemByItemId(itemId) <= 0) {
            throw new IllegalArgumentException("존재하지 않는 ITEM_ID입니다.");
        }

        Set<Integer> existingSeqSet = new HashSet<>(supportFeePayerManageDAO.selectArtitedSeqsByItemId(itemId));
        validateExistingSeq(seq, existingSeqSet, "삭제");

        String paySta = trimToEmpty(supportFeePayerManageDAO.selectArtitedPayStaByItemIdAndSeq(itemId, seq));
        if ("02".equals(paySta)) {
            throw new IllegalArgumentException("완납 건은 삭제하실 수 없습니다.");
        }
        if (!"01".equals(paySta)) {
            throw new IllegalArgumentException("미납 건만 삭제할 수 있습니다.");
        }

        supportFeePayerManageDAO.deleteArtitepByItemIdAndSeq(itemId, seq);
        supportFeePayerManageDAO.deleteArtitecByItemIdAndSeq(itemId, seq);
        int deletedArtited = supportFeePayerManageDAO.deleteArtitedByItemIdAndSeq(itemId, seq);
        if (deletedArtited <= 0) {
            throw new IllegalStateException("삭제 대상이 존재하지 않습니다.");
        }

        return new SupportFeePayerDeleteResponse(
                "00",
                egovMessageSource.getMessage("success.common.delete"),
                itemId,
                seq);
    }

    private SupportFeePayerArtitemInsertRequest buildBasicInfo(
            String itemId,
            String chgUserId,
            SupportFeePayerRegisterRequest request) {
        return buildBasicInfoRow(itemId, chgUserId, request.getBasicInfo());
    }

    private SupportFeePayerArtitemInsertRequest buildBasicInfoRow(
            String itemId,
            String chgUserId,
            SupportFeePayerBasicInfoRequest basicInfo) {
        SupportFeePayerArtitemInsertRequest basic = new SupportFeePayerArtitemInsertRequest();
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

    private static void validateExplicitSeqUnique(List<SupportFeePayerDetailRequest> details) {
        Set<Integer> seen = new HashSet<>();
        for (SupportFeePayerDetailRequest d : details) {
            if (d.getSeq() != null && d.getSeq() > 0) {
                if (!seen.add(d.getSeq())) {
                    throw new IllegalArgumentException("요청에 중복된 SEQ가 있습니다.");
                }
            }
        }
    }

    private static String resolveRowStatus(SupportFeePayerDetailRequest detail) {
        String status = trimToNull(detail.getRowStatus());
        if (status == null) {
            return detail.getSeq() != null && detail.getSeq() > 0 ? "U" : "I";
        }
        return status.toUpperCase();
    }

    private static void validateExistingSeq(Integer seq, Set<Integer> existingSeqSet, String actionName) {
        if (seq == null || seq <= 0) {
            throw new IllegalArgumentException(actionName + " 대상 detail.seq는 필수입니다.");
        }
        if (!existingSeqSet.contains(seq)) {
            throw new IllegalArgumentException(actionName + " 대상 SEQ가 존재하지 않습니다. seq=" + seq);
        }
    }

    /**
     * 수정/삭제 가능 여부: DB(ARTITED.PAY_STA)만 기준 — 미납({@code '01'})일 때 true.
     * 완납 건은 예외로 전체 요청을 실패시키지 않고 해당 행만 생략한다(배치 내 후속 I 등 처리).
     */
    private boolean isStoredDetailUnpaid(String itemId, int seq) {
        String paySta = trimToEmpty(supportFeePayerManageDAO.selectArtitedPayStaByItemIdAndSeq(itemId, seq));
        return "01".equals(paySta);
    }

    /**
     * ARTITED: INSERT … ON DUPLICATE KEY UPDATE.
     * ARTITEC: 동일 (ITEM_ID, SEQ) 산정목록을 삭제 후 요청 배열 순서대로 SEQ2=1..n 재등록.
     */
    private void upsertDetailBlock(
            String itemId,
            String chgUserId,
            SupportFeePayerDetailRequest detail,
            int seq,
            boolean runCostCalculation) {
        SupportFeePayerArtitedInsertRequest d = new SupportFeePayerArtitedInsertRequest();
        d.setItemId(itemId);
        d.setSeq(seq);
        d.setPaySta(trimToNull(detail.getPaySta()));
        d.setType1(trimToNull(detail.getType1()));
        d.setType2(trimToNull(detail.getType2()));
        d.setReqDate(parseDate(detail.getReqDate()));
        d.setBaseCost(zeroIfNull(detail.getBaseCost()));
        d.setWaterSum(detail.getWaterSum());
        d.setChgUserId(chgUserId);
        supportFeePayerManageDAO.upsertArtited(d);

        List<Integer> existingSeq2s = supportFeePayerManageDAO.selectArtitecSeq2sByItemIdAndSeq(itemId, seq);
        Set<Integer> existingSeq2Set = new HashSet<>(existingSeq2s);
        Integer nextSeq2Hint = supportFeePayerManageDAO.getNextArtitecSeq2(itemId, seq);
        int nextSeq2 = nextSeq2Hint != null ? nextSeq2Hint : 1;

        for (SupportFeePayerCalcRequest calc : detail.getCalculations()) {
            String calcRowStatus = resolveCalcRowStatus(calc);
            Integer requestSeq2 = calc.getSeq2();

            if ("D".equals(calcRowStatus)) {
                validateExistingSeq2(requestSeq2, existingSeq2Set, "삭제");
                supportFeePayerManageDAO.deleteArtitecByItemIdAndSeqAndSeq2(itemId, seq, requestSeq2);
                existingSeq2Set.remove(requestSeq2);
                continue;
            }

            SupportFeePayerArtitecInsertRequest c = new SupportFeePayerArtitecInsertRequest();
            c.setItemId(itemId);
            c.setSeq(seq);
            c.setFloor(zeroIfNull(calc.getFloor()));
            c.setBuildId(trimToNull(calc.getBuildId()));
            c.setRoomCnt(zeroIfNull(calc.getRoomCnt()));
            c.setHomeCnt(zeroIfNull(calc.getHomeCnt()));
            c.setBuildSize(calc.getBuildSize());
            c.setDayVal(calc.getDayVal());
            c.setCostYn(trimToNull(calc.getCostYn()));
            c.setWaterVol(calc.getWaterVol());
            c.setChgUserId(chgUserId);

            if ("U".equals(calcRowStatus)) {
                validateExistingSeq2(requestSeq2, existingSeq2Set, "수정");
                c.setSeq2(requestSeq2);
                int updated = supportFeePayerManageDAO.updateArtitec(c);
                if (updated <= 0) {
                    throw new IllegalStateException("산정 상세 수정에 실패했습니다. seq2=" + requestSeq2);
                }
                continue;
            }

            if ("I".equals(calcRowStatus)) {
                if (requestSeq2 != null && requestSeq2 > 0) {
                    throw new IllegalArgumentException("신규 계산행(I)에는 seq2를 지정할 수 없습니다.");
                }
                c.setSeq2(nextSeq2++);
                supportFeePayerManageDAO.insertArtitec(c);
                continue;
            }

            throw new IllegalArgumentException("calculation.rowStatus 값이 올바르지 않습니다. (I/U/D)");
        }

        if (runCostCalculation) {
            calculateAndUpdateCost(itemId, seq, chgUserId);
        }
    }

    /**
     * 삭제는 명시 요청된 SEQ만 수행한다.
     * 동시수정 상황에서 "요청에 없는 SEQ 전체 삭제"를 피하기 위해 단건 삭제 방식으로 처리.
     */
    private void deleteDetailBlock(String itemId, int seq) {
        supportFeePayerManageDAO.deleteArtitecByItemIdAndSeq(itemId, seq);
        supportFeePayerManageDAO.deleteArtitedByItemIdAndSeq(itemId, seq);
    }

    private static String resolveCalcRowStatus(SupportFeePayerCalcRequest calc) {
        String status = trimToNull(calc.getRowStatus());
        if (status == null) {
            return calc.getSeq2() != null && calc.getSeq2() > 0 ? "U" : "I";
        }
        return status.toUpperCase();
    }

    private static String resolvePaymentRowStatus(SupportFeePayerPaymentRequest payment) {
        String status = trimToNull(payment.getRowStatus());
        if (status == null) {
            return payment.getSeq2() != null && payment.getSeq2() > 0 ? "U" : "I";
        }
        return status.toUpperCase();
    }

    private static void validateExistingSeq2(Integer seq2, Set<Integer> existingSeq2Set, String actionName) {
        if (seq2 == null || seq2 <= 0) {
            throw new IllegalArgumentException(actionName + " 대상 calculation.seq2는 필수입니다.");
        }
        if (!existingSeq2Set.contains(seq2)) {
            throw new IllegalArgumentException(actionName + " 대상 계산행 SEQ2가 존재하지 않습니다. seq2=" + seq2);
        }
    }

    private static void validateExistingPaySeq2(Integer seq2, Set<Integer> existingSeq2Set, String actionName) {
        if (seq2 == null || seq2 <= 0) {
            throw new IllegalArgumentException(actionName + " 대상 납부내역.seq2는 필수입니다.");
        }
        if (!existingSeq2Set.contains(seq2)) {
            throw new IllegalArgumentException(actionName + " 대상 납부내역 SEQ2가 존재하지 않습니다. seq2=" + seq2);
        }
    }

    /**
     * f_cost('01', ITEM_ID, SEQ) 결과(원인자부담금|오수부가량)를 받아 ARTITED.WATER_COST/WATER_VAL 갱신.
     * 등록 직후, 그리고 이후 '계산' 버튼 API에서도 재사용 가능한 단위 함수.
     */
    private CostValues calculateAndUpdateCost(String itemId, int seq, String chgUserId) {
        SupportFeePayerCostCalcRequest procParam = new SupportFeePayerCostCalcRequest("01", itemId, seq);
        String procResult = supportFeePayerManageDAO.callCostProc(procParam);
        if (procResult == null || procResult.isBlank()) {
            throw new IllegalStateException("f_cost 응답이 비어 있습니다.");
        }

        String[] parts = procResult.split("\\|", -1);
        if (parts.length < 2) {
            throw new IllegalStateException("f_cost 응답 형식이 올바르지 않습니다. result=" + procResult);
        }

        Integer waterCost = parseInteger(parts[0]);
        BigDecimal waterVal = parseBigDecimal(parts[1]);

        SupportFeePayerArtitedCostUpdateRequest updateParam = new SupportFeePayerArtitedCostUpdateRequest();
        updateParam.setItemId(itemId);
        updateParam.setSeq(seq);
        updateParam.setWaterCost(waterCost);
        updateParam.setWaterVal(waterVal);
        updateParam.setChgUserId(chgUserId);
        supportFeePayerManageDAO.updateArtitedCost(updateParam);
        BigDecimal waterSum = supportFeePayerManageDAO.selectArtitecWaterSum(itemId, seq);
        return new CostValues(waterCost, waterVal, waterSum != null ? waterSum : BigDecimal.ZERO);
    }

    private record CostValues(Integer waterCost, BigDecimal waterVal, BigDecimal waterSum) {
    }

    private record SaveResult(
            String itemId,
            boolean isNewItem,
            Integer targetSeqForCalculate,
            List<SupportFeePayerRegisterSkippedDetailResponse> skippedDetails) {
    }

    private static Integer zeroIfNull(Integer n) {
        return n == null ? 0 : n;
    }

    private static Integer parseInteger(String raw) {
        String v = trimToNull(raw);
        if (v == null) {
            return 0;
        }
        return Integer.parseInt(v.replace(",", ""));
    }

    private static BigDecimal parseBigDecimal(String raw) {
        String v = trimToNull(raw);
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(v.replace(",", ""));
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

    private static Date parseDate(String raw) {
        String value = trimToNull(raw);
        if (value == null) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(value, DATE_FORMATTER);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("통지일 형식이 올바르지 않습니다. (yyyy-MM-dd)");
        }
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
}
