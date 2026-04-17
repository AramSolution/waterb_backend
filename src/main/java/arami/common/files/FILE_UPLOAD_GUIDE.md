# 파일 업로드 API 가이드 (common/files)

## 개요

`arami.common.files` 패키지는 파일 업로드/삭제/수정을 위한 공통 모듈입니다.

## 구성

- **FileUtil**: 파일 저장, 썸네일 생성, 삭제 유틸
- **FileManageService** / **FileManageServiceImpl**: 파일 DB·파일시스템 처리
- **FileManageController**: REST API (`/api/v1/files/*`)

## API 엔드포인트

| 메서드 | 경로                   | 설명                                               |
| ------ | ---------------------- | -------------------------------------------------- |
| POST   | `/api/v1/files/upload` | 다중 파일 업로드 (fileId 없으면 자동 생성)         |
| POST   | `/api/v1/files/append` | 기존 fileId에 파일 추가 (fileId 필수)              |
| POST   | `/api/v1/files/delete` | 파일 삭제 (fileId + seq: 1건, fileId만: 그룹 전체) |
| POST   | `/api/v1/files/update` | 파일 교체 (fileId_seq 키로 멀티 업데이트)          |

## DB 테이블

- **ARTFILE**: 파일 메타 정보 (FILE_ID, SEQ, FILE_PATH, SAVE_NM, ORGF_NM 등)

## 설정 (application.properties)

```properties
Globals.fileStorePath=./files
Globals.posblAtchFileSize=5242880
Globals.fileUpload.Extensions=.gif.jpg.jpeg.png.xls.xlsx
Globals.fileUpload.Extensions.Images=.gif.jpg.jpeg.png
tsid.node.id=0
```

## 참고

- **arami.common.error**: BusinessException, ErrorCode, ResultResponse
- **arami.common.util.TsidUtil**: 파일 ID 생성 (TSID)
- **arami.common.config.TsidConfig**: TsidFactory Bean
