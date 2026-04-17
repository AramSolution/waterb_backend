package arami.common.auth.service.impl;

public enum RoleType {
    PERMIT_ALL, // 모든권한
    ROLE,       // 특정권한
    ANY_ROLE,   // 복수권한
    AUTHENTICATED // 인가된 사용자
}