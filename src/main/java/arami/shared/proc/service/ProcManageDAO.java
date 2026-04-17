package arami.shared.proc.service;

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
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공통 프로시저/함수 호출 DAO
 */
@Repository("procManageDAO")
public class ProcManageDAO extends EgovAbstractMapper {

    /**
     * f_changlist: 변경 이력 조회 (1건)
     */
    public ChanglistResponse callChanglist(ChanglistRequest request) {
        return selectOne("procManageDAO.callChanglist", request);
    }

    /**
     * f_check: 자격 조건 확인 (Y/N|코드/E)
     */
    public CheckResponse selectCheck(CheckRequest request) {
        return selectOne("procManageDAO.selectCheck", request);
    }

    /**
     * f_choicelist: 랜덤 신청자 선택 (다건)
     */
    public List<ChoiceListResponse> callChoiceList(ChoiceListRequest request) {
        return selectList("procManageDAO.callChoiceList", request);
    }

    /**
     * f_choicelist: 랜덤 신청자 선택 (순위 미포함 응답, 다건)
     */
    public List<ChoiceListNonRankResponse> callChoiceListNonRank(ChoiceListRequest request) {
        return selectList("procManageDAO.callChoiceListNonRank", request);
    }

    /**
     * f_selectlist01: 사업ID·기준년월별 목록 (다건)
     */
    public List<SelectList01ItemResponse> callSelectList01(SelectList01Request request) {
        return selectList("procManageDAO.callSelectList01", request);
    }

    /**
     * f_selectlist02: 상담일자별 상담장소/시간 목록 (다건)
     */
    public List<SelectList02ItemResponse> callSelectList02(SelectList02Request request) {
        return selectList("procManageDAO.callSelectList02", request);
    }
}
