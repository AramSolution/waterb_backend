package arami.adminWeb.artchoi.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import arami.adminWeb.artchoi.service.ArtchoiManageDAO;
import arami.adminWeb.artchoi.service.ArtchoiManageService;
import arami.adminWeb.artchoi.service.dto.request.ArtchoiResultGbUpdateParam;
import arami.adminWeb.artchoi.service.dto.response.ArtchoiListItemResponse;

@Service("artchoiManageService")
public class ArtchoiManageServiceImpl implements ArtchoiManageService {

    @Resource(name = "artchoiManageDAO")
    private ArtchoiManageDAO artchoiManageDAO;

    private void withArtchoiTableLock(Runnable action) {
        Integer lockResult = artchoiManageDAO.acquireArtchoiInsertLock();
        if (lockResult == null || lockResult != 1) {
            throw new IllegalStateException("ARTCHOI 테이블 잠금 획득에 실패했습니다.");
        }
        try {
            action.run();
        } finally {
            artchoiManageDAO.releaseArtchoiInsertLock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertArtchoiResultGbBatch(List<ArtchoiResultGbUpdateParam> list, String chgUserId) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<ArtchoiResultGbUpdateParam> saveList = new java.util.ArrayList<>();
        for (ArtchoiResultGbUpdateParam item : list) {
            if (item == null) {
                continue;
            }
            ArtchoiResultGbUpdateParam param = new ArtchoiResultGbUpdateParam();
            param.setResultGb(item.getResultGb());
            param.setBaseId(item.getBaseId());
            param.setItem1(item.getItem1());
            param.setItem2(item.getItem2());
            param.setItem3(item.getItem3());
            param.setItem4(item.getItem4());
            param.setItem5(item.getItem5());
            param.setItem6(item.getItem6());
            param.setItem7(item.getItem7());
            param.setItem8(item.getItem8());
            param.setItem9(item.getItem9());
            param.setItem10(item.getItem10());
            param.setItem11(item.getItem11());
            param.setItem12(item.getItem12());
            param.setItem13(item.getItem13());
            param.setItem14(item.getItem14());
            param.setItem15(item.getItem15());
            param.setItem16(item.getItem16());
            param.setItem17(item.getItem17());
            param.setItem18(item.getItem18());
            param.setItem19(item.getItem19());
            param.setItem20(item.getItem20());
            saveList.add(param);
        }
        if (!saveList.isEmpty()) {
            withArtchoiTableLock(() -> {
                artchoiManageDAO.deleteAllArtchoi();
                artchoiManageDAO.insertArtchoiResultGb(saveList);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResultGbByChoiSeqList(List<Integer> choiSeqList, String resultGb, String chgUserId) {
        if (choiSeqList == null || choiSeqList.isEmpty()) {
            return;
        }
        withArtchoiTableLock(() -> artchoiManageDAO.updateResultGbByChoiSeqList(choiSeqList, resultGb));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtchoiListItemResponse> getAllArtchoi() {
        AtomicReference<List<ArtchoiListItemResponse>> holder = new AtomicReference<>();
        withArtchoiTableLock(() -> holder.set(artchoiManageDAO.selectAllArtchoi()));
        return holder.get();
    }
}
