package arami.adminWeb.artprom.service.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ArtpromMentorApplicationBusinessListResponse {

    private String result;
    private String message;
    private List<ArtpromMentorApplicationBusinessItem> data;
}
