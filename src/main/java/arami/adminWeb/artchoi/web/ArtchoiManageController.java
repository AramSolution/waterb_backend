package arami.adminWeb.artchoi.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import arami.common.CommonService;
import arami.adminWeb.artchoi.service.ArtchoiManageService;
import arami.adminWeb.artchoi.service.dto.request.ArtchoiSelectionBatchInsertRequest;
import arami.adminWeb.artchoi.service.dto.response.ArtchoiListItemResponse;
import arami.adminWeb.artchoi.service.dto.response.ArtchoiResultResponse;
import arami.shared.proc.dto.request.ChoiceListRequest;
import arami.shared.proc.dto.response.ChoiceListNonRankResponse;
import arami.shared.proc.service.ProcService;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin/artchoi")
public class ArtchoiManageController extends CommonService {

    @Resource
    private ArtchoiManageService artchoiManageService;
    @Resource(name = "procService")
    private ProcService procService;

    @GetMapping(value = "", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<ArtchoiListItemResponse>> getAllArtchoi() {
        List<ArtchoiListItemResponse> response = artchoiManageService.getAllArtchoi();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/selection-insert", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ArtchoiResultResponse> insertArtchoiResultGbBatch(
            @RequestBody @Valid ArtchoiSelectionBatchInsertRequest request) {
        ArtchoiResultResponse response = new ArtchoiResultResponse();
        try {
            artchoiManageService.insertArtchoiResultGbBatch(request.getList(), getCurrentUniqId());

            int selectCnt = Objects.requireNonNullElse(request.getSelectCnt(), 0);
            int reserveCnt = Objects.requireNonNullElse(request.getReserveCnt(), 0);
            int dataCnt = selectCnt + reserveCnt;

            // 프로시저 f_choicelist 호출
            ChoiceListRequest choiceListRequest = new ChoiceListRequest();
            choiceListRequest.setAGubun("02");
            choiceListRequest.setAProId("");
            choiceListRequest.setAProSeq(0);
            choiceListRequest.setADataCnt(dataCnt);
            choiceListRequest.setARank("00|00|00|00");
            List<ChoiceListNonRankResponse> choiceList = procService.getChoiceListNonRank(choiceListRequest);
            log.info("insertArtchoiResultGbBatch -> choiceList executed, size={}", choiceList == null ? 0 : choiceList.size());

            if (choiceList != null && !choiceList.isEmpty()) {
                List<Integer> choiSeqList = choiceList.stream()
                    .map(ChoiceListNonRankResponse::getChoiSeq)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
                int selectedSize = Math.min(Math.max(selectCnt, 0), choiSeqList.size());
                List<Integer> yTargetList = new ArrayList<>(choiSeqList.subList(0, selectedSize));
                List<Integer> rTargetList = new ArrayList<>(choiSeqList.subList(selectedSize, choiSeqList.size()));
                artchoiManageService.updateResultGbByChoiSeqList(yTargetList, "Y", getCurrentUniqId());
                artchoiManageService.updateResultGbByChoiSeqList(rTargetList, "R", getCurrentUniqId());
                log.info("insertArtchoiResultGbBatch -> RESULT_GB updated, Y size={}, R size={}",
                    yTargetList.size(), rTargetList.size());
            } else {
                System.out.println("choice-list 응답 상세 : 빈 배열 또는 null");
            }

            response.setResult("00");
            response.setMessage("선정여부가 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("insertArtchoiResultGbBatch: {}", e.getMessage(), e);
            response.setResult("01");
            response.setMessage("선정여부 저장 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
