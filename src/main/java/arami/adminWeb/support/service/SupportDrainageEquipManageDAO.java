package arami.adminWeb.support.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequmInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdInsertRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdPayStaSyncParam;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequdPayStaUpdateRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipArtequpSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipListRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipUnpaidListRequest;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailItemResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipListItemResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailRowResponse;

@Repository("supportDrainageEquipManageDAO")
public class SupportDrainageEquipManageDAO extends EgovAbstractMapper {

    public String getNextItemId() {
        return selectOne("supportDrainageEquipManageDAO.getNextItemId", null);
    }

    public int insertArtequm(SupportDrainageEquipArtequmInsertRequest request) {
        return insert("supportDrainageEquipManageDAO.insertArtequm", request);
    }

    public int countArtequmByItemId(String itemId) {
        Integer n = selectOne("supportDrainageEquipManageDAO.countArtequmByItemId", itemId);
        return n != null ? n : 0;
    }

    public int deleteArtequmByItemId(String itemId) {
        return delete("supportDrainageEquipManageDAO.deleteArtequmByItemId", itemId);
    }

    public int updateArtequmBasic(SupportDrainageEquipArtequmInsertRequest request) {
        return update("supportDrainageEquipManageDAO.updateArtequmBasic", request);
    }

    public Integer getNextArtequdSeq(String itemId) {
        return selectOne("supportDrainageEquipManageDAO.getNextArtequdSeq", itemId);
    }

    public List<Integer> selectArtequdSeqsByItemId(String itemId) {
        List<?> raw = selectList("supportDrainageEquipManageDAO.selectArtequdSeqsByItemId", itemId);
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

    public int countUnpaidArtequdByItemId(String itemId) {
        Integer n = selectOne("supportDrainageEquipManageDAO.countUnpaidArtequdByItemId", itemId);
        return n != null ? n : 0;
    }

    public String selectArtequdPayStaByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportDrainageEquipManageDAO.selectArtequdPayStaByItemIdAndSeq", param);
    }

    public int upsertArtequd(SupportDrainageEquipArtequdInsertRequest request) {
        return insert("supportDrainageEquipManageDAO.upsertArtequd", request);
    }

    public int deleteArtequpByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return delete("supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeq", param);
    }

    public int deleteArtequdByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return delete("supportDrainageEquipManageDAO.deleteArtequdByItemIdAndSeq", param);
    }

    public Integer getNextArtequpSeq2(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        return selectOne("supportDrainageEquipManageDAO.getNextArtequpSeq2", param);
    }

    public List<Integer> selectArtequpSeq2sByItemIdAndSeq(String itemId, int seq) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("seq", seq);
        List<?> raw = selectList("supportDrainageEquipManageDAO.selectArtequpSeq2sByItemIdAndSeq", param);
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

    public int insertArtequp(SupportDrainageEquipArtequpSaveRequest request) {
        return insert("supportDrainageEquipManageDAO.insertArtequp", request);
    }

    public int updateArtequp(SupportDrainageEquipArtequpSaveRequest request) {
        return update("supportDrainageEquipManageDAO.updateArtequp", request);
    }

    public int deleteArtequpByItemIdAndSeqAndSeq2(String itemId, int seq, int seq2) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("itemId", itemId);
        param.put("seq", seq);
        param.put("seq2", seq2);
        return delete("supportDrainageEquipManageDAO.deleteArtequpByItemIdAndSeqAndSeq2", param);
    }

    public int updateArtequdPaySta(SupportDrainageEquipArtequdPayStaUpdateRequest request) {
        return update("supportDrainageEquipManageDAO.updateArtequdPaySta", request);
    }

    public int updateArtequdEquipPayByItemId(String itemId) {
        return update("supportDrainageEquipManageDAO.updateArtequdEquipPayByItemId", itemId);
    }

    public int updateArtequdPayStaByEquipPayVsCost(SupportDrainageEquipArtequdPayStaSyncParam param) {
        return update("supportDrainageEquipManageDAO.updateArtequdPayStaByEquipPayVsCost", param);
    }

    public int selectDrainageEquipListCount(SupportDrainageEquipListRequest request) {
        Integer count = selectOne("supportDrainageEquipManageDAO.selectDrainageEquipListCount", request);
        return count != null ? count : 0;
    }

    public List<SupportDrainageEquipListItemResponse> selectDrainageEquipList(
            SupportDrainageEquipListRequest request) {
        return selectDrainageEquipListByStatement("supportDrainageEquipManageDAO.selectDrainageEquipList", request);
    }

    public List<SupportDrainageEquipListItemResponse> selectDrainageEquipExcelList(
            SupportDrainageEquipListRequest request) {
        return selectDrainageEquipListByStatement(
                "supportDrainageEquipManageDAO.selectDrainageEquipExcelList", request);
    }

    public int selectDrainageEquipUnpaidListCount(SupportDrainageEquipUnpaidListRequest request) {
        Integer count = selectOne("supportDrainageEquipManageDAO.selectDrainageEquipUnpaidListCount", request);
        return count != null ? count : 0;
    }

    public List<SupportDrainageEquipListItemResponse> selectDrainageEquipUnpaidList(
            SupportDrainageEquipUnpaidListRequest request) {
        return selectDrainageEquipListByStatement(
                "supportDrainageEquipManageDAO.selectDrainageEquipUnpaidList", request);
    }

    public SupportDrainageEquipDetailDataResponse selectDrainageEquipBasicDetailByItemId(String itemId) {
        return selectOne("supportDrainageEquipManageDAO.selectDrainageEquipBasicDetailByItemId", itemId);
    }

    @SuppressWarnings("unchecked")
    public List<SupportDrainageEquipDetailItemResponse> selectDrainageEquipDetailListByItemId(String itemId) {
        return (List<SupportDrainageEquipDetailItemResponse>) (List<?>) selectList(
                "supportDrainageEquipManageDAO.selectDrainageEquipDetailListByItemId", itemId);
    }

    public SupportDrainageEquipPaymentDetailDataResponse selectDrainageEquipPaymentBasicDetailByItemId(
            String itemId) {
        return selectOne("supportDrainageEquipManageDAO.selectDrainageEquipPaymentBasicDetailByItemId", itemId);
    }

    @SuppressWarnings("unchecked")
    public List<SupportDrainageEquipPaymentDetailRowResponse> selectDrainageEquipPaymentDetailRowsByItemId(
            String itemId) {
        return (List<SupportDrainageEquipPaymentDetailRowResponse>) (List<?>) selectList(
                "supportDrainageEquipManageDAO.selectDrainageEquipPaymentDetailRowsByItemId", itemId);
    }

    private List<SupportDrainageEquipListItemResponse> selectDrainageEquipListByStatement(
            String statementId, Object request) {
        List<?> raw = selectList(statementId, request);
        List<SupportDrainageEquipListItemResponse> out = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return out;
        }
        for (Object o : raw) {
            if (o instanceof SupportDrainageEquipListItemResponse item) {
                out.add(item);
            }
        }
        return out;
    }
}
