package arami.adminWeb.artchoi.service;

import java.util.List;

import arami.adminWeb.artchoi.service.dto.request.ArtchoiResultGbUpdateParam;
import arami.adminWeb.artchoi.service.dto.response.ArtchoiListItemResponse;

public interface ArtchoiManageService {
    void insertArtchoiResultGbBatch(List<ArtchoiResultGbUpdateParam> list, String chgUserId);
    void updateResultGbByChoiSeqList(List<Integer> choiSeqList, String resultGb, String chgUserId);
    List<ArtchoiListItemResponse> getAllArtchoi();
}
