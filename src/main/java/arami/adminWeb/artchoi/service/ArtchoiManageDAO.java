package arami.adminWeb.artchoi.service;

import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.adminWeb.artchoi.service.dto.request.ArtchoiResultGbUpdateParam;
import arami.adminWeb.artchoi.service.dto.response.ArtchoiListItemResponse;

@Repository("artchoiManageDAO")
public class ArtchoiManageDAO extends EgovAbstractMapper {

    public Integer acquireArtchoiInsertLock() {
        return selectOne("artchoiManageDAO.acquireArtchoiInsertLock");
    }

    public Integer releaseArtchoiInsertLock() {
        return selectOne("artchoiManageDAO.releaseArtchoiInsertLock");
    }

    public int deleteAllArtchoi() {
        return delete("artchoiManageDAO.deleteAllArtchoi");
    }

    public int insertArtchoiResultGb(List<ArtchoiResultGbUpdateParam> list) {
        return insert("artchoiManageDAO.insertArtchoiResultGb", list);
    }

    public int updateResultGbByChoiSeqList(List<Integer> choiSeqList, String resultGb) {
        return update("artchoiManageDAO.updateResultGbByChoiSeqList",
            Map.of("choiSeqList", choiSeqList, "resultGb", resultGb));
    }

    public List<ArtchoiListItemResponse> selectAllArtchoi() {
        return selectList("artchoiManageDAO.selectAllArtchoi");
    }
}
