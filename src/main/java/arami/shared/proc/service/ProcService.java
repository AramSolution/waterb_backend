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

import java.util.List;

/**
 * 공통 프로시저/함수 호출 Service (관리자·사용자 공통)
 */
public interface ProcService {

    /**
     * f_changlist: 변경 이력 조회 (1건)
     */
    ChanglistResponse getChanglist(ChanglistRequest request);

    /**
     * f_check: 자격 조건 확인 (Y / N|코드 / E)
     */
    CheckResponse getCheck(CheckRequest request);

    /**
     * f_choicelist: 랜덤 신청자 선택 (다건)
     */
    List<ChoiceListResponse> getChoiceList(ChoiceListRequest request);

    /**
     * f_choicelist: 랜덤 신청자 선택 (순위 미포함 응답)
     */
    List<ChoiceListNonRankResponse> getChoiceListNonRank(ChoiceListRequest request);

    /**
     * f_selectlist01: 사업ID·기준년월별 목록 (다건)
     */
    List<SelectList01ItemResponse> getSelectList01(SelectList01Request request);

    /**
     * f_selectlist02: 상담일자별 상담장소/시간 목록 (다건)
     */
    List<SelectList02ItemResponse> getSelectList02(SelectList02Request request);
}
