package arami.adminWeb.support.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class SupportFeePayerListResponse {

    private List<SupportFeePayerListItemResponse> data;
    private String result;
    private String message;
}
