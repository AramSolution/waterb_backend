package arami.adminWeb.support.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitemInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitecInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitepSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedPayStaUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedCostUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerArtitedInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerCostCalcRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailCalculationResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailRowResponse;

@Repository("supportFeePayerManageDAO")
public class SupportFeePayerManageDAO extends EgovAbstractMapper {

    public String getNextItemId() {
        return selectOne("supportFeePayerManageDAO.getNextItemId", null);
    }

    public int insertArtitem(SupportFeePayerArtitemInsertRequest request) {
        return insert("supportFeePayerManageDAO.insertArtitem", request);
    }

    public int countArtitemByItemId(String itemId) {
        Integer n = selectOne("supportFeePayerManageDAO.countArtitemByItemId", itemId);
        return n != null ? n : 0;
    }

    public int updateArtitemBasic(SupportFeePayerArtitemInsertRequest request) {
        return update("supportFeePayerManageDAO.updateArtitemBasic", request);
    }

    public Integer getNextArtitedSeq(String itemId) {
        return selectOne("supportFeePayerManageDAO.getNextArtitedSeq", itemId);
    }

    public List<Integer> selectArtitedSeqsByItemId(String itemId) {
        List<?> raw = selectList("supportFeePayerManageDAO.selectArtitedSeqsByItemId", itemId);
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> out = new ArrayList<>(raw.size());
        for (Object o : raw) {
            if (o instanceof Number num) {
                out.add(num.intValue());
            }
        }
        return out;
    }

    public int countUnpaidArtitedByItemId(String itemId) {
        Integer n = selectOne("supportFeePayerManageDAO.countUnpaidArtitedByItemId", itemId);
        return n != null ? n : 0;
    }

    /**
     * ARTITED에 저장된 납부상태 (미납 판별은 서비스에서 PAY_STA = '01' 과 비교).
     */
    public String selectArtitedPayStaByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportFeePayerManageDAO.selectArtitedPayStaByItemIdAndSeq", param);
    }

    public int insertArtited(SupportFeePayerArtitedInsertRequest request) {
        return insert("supportFeePayerManageDAO.insertArtited", request);
    }

    public int upsertArtited(SupportFeePayerArtitedInsertRequest request) {
        return insert("supportFeePayerManageDAO.upsertArtited", request);
    }

    public int deleteArtitecByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return delete("supportFeePayerManageDAO.deleteArtitecByItemIdAndSeq", param);
    }

    public Integer getNextArtitecSeq2(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportFeePayerManageDAO.getNextArtitecSeq2", param);
    }

    public Integer getNextArtitepSeq2(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportFeePayerManageDAO.getNextArtitepSeq2", param);
    }

    public List<Integer> selectArtitecSeq2sByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        List<?> raw = selectList("supportFeePayerManageDAO.selectArtitecSeq2sByItemIdAndSeq", param);
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> out = new ArrayList<>(raw.size());
        for (Object o : raw) {
            if (o instanceof Number num) {
                out.add(num.intValue());
            }
        }
        return out;
    }

    public List<Integer> selectArtitepSeq2sByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        List<?> raw = selectList("supportFeePayerManageDAO.selectArtitepSeq2sByItemIdAndSeq", param);
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> out = new ArrayList<>(raw.size());
        for (Object o : raw) {
            if (o instanceof Number num) {
                out.add(num.intValue());
            }
        }
        return out;
    }

    public int updateArtitec(SupportFeePayerArtitecInsertRequest request) {
        return update("supportFeePayerManageDAO.updateArtitec", request);
    }

    public int updateArtitep(SupportFeePayerArtitepSaveRequest request) {
        return update("supportFeePayerManageDAO.updateArtitep", request);
    }

    public int deleteArtitecByItemIdAndSeqAndSeq2(String itemId, int seq, int seq2) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("itemId", itemId);
        param.put("seq", seq);
        param.put("seq2", seq2);
        return delete("supportFeePayerManageDAO.deleteArtitecByItemIdAndSeqAndSeq2", param);
    }

    public int deleteArtitepByItemIdAndSeqAndSeq2(String itemId, int seq, int seq2) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("itemId", itemId);
        param.put("seq", seq);
        param.put("seq2", seq2);
        return delete("supportFeePayerManageDAO.deleteArtitepByItemIdAndSeqAndSeq2", param);
    }

    public int deleteArtitedByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return delete("supportFeePayerManageDAO.deleteArtitedByItemIdAndSeq", param);
    }

    public String callCostProc(SupportFeePayerCostCalcRequest request) {
        return selectOne("supportFeePayerManageDAO.callCostProc", request);
    }

    public int updateArtitedCost(SupportFeePayerArtitedCostUpdateRequest request) {
        return update("supportFeePayerManageDAO.updateArtitedCost", request);
    }

    public BigDecimal selectArtitecWaterSum(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportFeePayerManageDAO.selectArtitecWaterSum", param);
    }

    public int insertArtitec(SupportFeePayerArtitecInsertRequest request) {
        return insert("supportFeePayerManageDAO.insertArtitec", request);
    }

    public int insertArtitep(SupportFeePayerArtitepSaveRequest request) {
        return insert("supportFeePayerManageDAO.insertArtitep", request);
    }

    public int updateArtitedWaterPayByItemId(String itemId) {
        return update("supportFeePayerManageDAO.updateArtitedWaterPayByItemId", itemId);
    }

    public int updateArtitedPaySta(SupportFeePayerArtitedPayStaUpdateRequest request) {
        return update("supportFeePayerManageDAO.updateArtitedPaySta", request);
    }

    public List<SupportFeePayerListItemResponse> selectFeePayerList(SupportFeePayerListRequest request) {
        List<?> raw = selectList("supportFeePayerManageDAO.selectFeePayerList", request);
        List<SupportFeePayerListItemResponse> out = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return out;
        }
        for (Object o : raw) {
            if (o instanceof SupportFeePayerListItemResponse item) {
                out.add(item);
            }
        }
        return out;
    }

    public SupportFeePayerDetailDataResponse selectFeePayerBasicDetailByItemId(String itemId) {
        return selectOne("supportFeePayerManageDAO.selectFeePayerBasicDetailByItemId", itemId);
    }

    public SupportFeePayerPaymentDetailDataResponse selectFeePayerPaymentBasicDetailByItemId(String itemId) {
        return selectOne("supportFeePayerManageDAO.selectFeePayerPaymentBasicDetailByItemId", itemId);
    }

    @SuppressWarnings("unchecked")
    public List<SupportFeePayerDetailItemResponse> selectFeePayerDetailListByItemId(String itemId) {
        return (List<SupportFeePayerDetailItemResponse>) (List<?>) selectList(
                "supportFeePayerManageDAO.selectFeePayerDetailListByItemId",
                itemId);
    }

    @SuppressWarnings("unchecked")
    public List<SupportFeePayerDetailCalculationResponse> selectFeePayerCalculationListByItemId(String itemId) {
        return (List<SupportFeePayerDetailCalculationResponse>) (List<?>) selectList(
                "supportFeePayerManageDAO.selectFeePayerCalculationListByItemId",
                itemId);
    }

    @SuppressWarnings("unchecked")
    public List<SupportFeePayerPaymentDetailRowResponse> selectFeePayerPaymentDetailRowsByItemId(String itemId) {
        return (List<SupportFeePayerPaymentDetailRowResponse>) (List<?>) selectList(
                "supportFeePayerManageDAO.selectFeePayerPaymentDetailRowsByItemId",
                itemId);
    }
}
