package arami.shared.proc.service.impl;

import arami.shared.proc.dto.request.ChanglistRequest;
import arami.shared.proc.dto.request.CheckRequest;
import arami.shared.proc.dto.request.ChoiceListRequest;
import arami.shared.proc.dto.request.SelectList01Request;
import arami.shared.proc.dto.request.SelectList02Request;
import arami.shared.proc.dto.response.ChanglistResponse;
import arami.shared.proc.dto.response.CheckResponse;
import arami.shared.proc.dto.response.ChoiceListNonRankResponse;
import arami.shared.proc.dto.response.ChoiceListResponse;
import arami.shared.proc.dto.response.SelectList01ItemResponse;
import arami.shared.proc.dto.response.SelectList02ItemResponse;
import arami.shared.proc.service.ProcManageDAO;
import arami.shared.proc.service.ProcService;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 공통 프로시저/함수 호출 Service 구현
 */
@Service("procService")
public class ProcServiceImpl extends EgovAbstractServiceImpl implements ProcService {

    @Resource(name = "procManageDAO")
    private ProcManageDAO procManageDAO;

    @Override
    public ChanglistResponse getChanglist(ChanglistRequest request) {
        return procManageDAO.callChanglist(request);
    }

    @Override
    public CheckResponse getCheck(CheckRequest request) {
        return procManageDAO.selectCheck(request);
    }

    @Override
    public List<ChoiceListResponse> getChoiceList(ChoiceListRequest request) {
        return procManageDAO.callChoiceList(request);
    }

    @Override
    public List<ChoiceListNonRankResponse> getChoiceListNonRank(ChoiceListRequest request) {
        return procManageDAO.callChoiceListNonRank(request);
    }

    @Override
    public List<SelectList01ItemResponse> getSelectList01(SelectList01Request request) {
        return procManageDAO.callSelectList01(request);
    }

    @Override
    public List<SelectList02ItemResponse> getSelectList02(SelectList02Request request) {
        return procManageDAO.callSelectList02(request);
    }
}
