package arami.common.auth.service.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "권한 URL패턴 정보 VO")
@Getter
@Setter
@ToString
public class AuthRoleDTO {

    @Schema(description = "롤코드")
    private int roleCode = 0;

    @Schema(description = "롤타입")
    private String roleType = "";

    @Schema(description = "롤이름")
    private String roleName = "";

    @Schema(description = "HTTP 메서드")
    private RoleType httpMethod;

    @Schema(description = "롤패턴")
    private String rolePttrn = "";

    @Schema(description = "롤설명")
    private String roleDesc = "";

    @Schema(description = "규칙순서")
    private int sortNo = 0;

    @Schema(description = "상태코드")
    private String sttusCode = "";

}