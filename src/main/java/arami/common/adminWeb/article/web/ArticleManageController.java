package arami.common.adminWeb.article.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import egovframework.com.cmm.LoginVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringEscapeUtils;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import arami.common.CommonService;
import arami.common.adminWeb.article.service.ArticleManageService;
import arami.common.adminWeb.board.service.BoardMasterService;
import arami.common.error.BusinessException;
import arami.common.files.FileUtil;
import arami.common.files.service.FileDTO;
import arami.common.files.service.FileManageService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.let.utl.sim.service.EgovFileScrty;

/**
 * @Class  Name : ArticleManageController.java
 * @Description : [관리자] 게시판 게시글의 추가,수정, 조회, 삭제 등의 관리 작업
 * @Modification  Information
 *
 * @    수정일       수정자                                 수정내용
 * @ ----------   --------  ------------------------------------------------------------
 * @ 2024.07.22    정우민     최초 생성 ( 위펫 참고 )
 * @ 2025.12.30    수정       스프링 부트 형식으로 변경
 *
 *  @author 아람솔루션
 *  @since 2024.07.22
 *  @version 2.0
 *  @see
 */
@Slf4j
@RestController
@RequestMapping("/api/cont/bord")
public class ArticleManageController {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
    private EgovMessageSource egovMessageSource;

	@Autowired
	private BoardMasterService boardMasterService;

	@Autowired
	private ArticleManageService articleManageService;

	@Resource(name = "fileUtil")
	private FileUtil fileUtil;

	@Resource(name = "fileManageService")
	private FileManageService fileManageService;

	// CommonService를 사용하여 setCommon 메서드 제공
	private CommonService commonService = new CommonService();

	protected void setCommon(HttpServletRequest request, ModelMap model) throws Exception {
		commonService.setCommon(request, model);
	}

	/**
	 * 프론트 multipart 필드 archiveImageFileSeqs(JSON 배열 문자열, 예: [0,1,2]) 파싱.
	 * 없거나 파싱 실패 시 null → FileUtil에서 0,1,2… 자동 부여.
	 */
	private List<Integer> parseArchiveImageFileSeqs(ModelMap model) {
		Object raw = model.get("archiveImageFileSeqs");
		if (raw == null) {
			return null;
		}
		String s = raw.toString().trim();
		if (s.isEmpty()) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(s, new TypeReference<List<Integer>>() { });
		} catch (Exception e) {
			log.warn("parseArchiveImageFileSeqs: invalid JSON {}", s);
			return null;
		}
	}

	/** model·request 후보 중 비어 있지 않은 첫 문자열 (multipart 파라미터 누락 보완). */
	private static String stringParamFirst(Object... candidates) {
		if (candidates == null) {
			return "";
		}
		for (Object c : candidates) {
			if (c == null) {
				continue;
			}
			String s = String.valueOf(c).trim();
			if (!s.isEmpty()) {
				return s;
			}
		}
		return "";
	}

	private static int parseJsonInt(Object o) {
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		return Integer.parseInt(String.valueOf(o).trim());
	}

	/**
	 * 프론트 archiveImageFileReplaceSeqs(JSON): multipart archiveImageFiles 와 동일 길이, -1=append, &gt;=0=해당 SEQ 행 교체.
	 */
	private List<Integer> parseArchiveImageReplaceSeqs(ModelMap model, HttpServletRequest request) {
		String s = stringParamFirst(model.get("archiveImageFileReplaceSeqs"),
				request != null ? request.getParameter("archiveImageFileReplaceSeqs") : null);
		if (s.isEmpty()) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(s, new TypeReference<List<Integer>>() { });
		} catch (Exception e) {
			log.warn("parseArchiveImageReplaceSeqs: invalid JSON {}", s);
			return null;
		}
	}

	/**
	 * 프론트 archiveImageOrder(JSON): [{ "type":"existing", "seq":0 }, { "type":"new", "index":0 }, …]
	 * type=new 의 index는 이번 요청의 archiveImageFiles 중 **INSERT만** 집계한 배열 인덱스(0부터). 교체(updateFile)는 포함하지 않음.
	 * multipart 요청에서 일부 필드가 getParameterNames()에 안 잡히는 환경 대비: request.getParameter로 보조 조회.
	 * 빈 배열([])이면 NTT_IMG 그룹 전체 삭제 후 nttImgFileId 비움.
	 */
	private void applyArchiveImageOrder(HttpServletRequest request, ModelMap model, List<Integer> newUploadSeqs)
			throws Exception {
		String json = stringParamFirst(model.get("archiveImageOrder"), request.getParameter("archiveImageOrder"));
		if (json.isEmpty()) {
			return;
		}
		List<java.util.Map<String, Object>> items;
		try {
			items = OBJECT_MAPPER.readValue(json, new TypeReference<List<java.util.Map<String, Object>>>() { });
		} catch (Exception e) {
			log.warn("applyArchiveImageOrder: invalid JSON {}", json);
			return;
		}
		String fidStr = stringParamFirst(model.get("nttImgFileId"), model.get("NTT_IMG_FILE_ID"),
				request.getParameter("nttImgFileId"), request.getParameter("NTT_IMG_FILE_ID"));
		if (fidStr.isEmpty()) {
			log.warn("applyArchiveImageOrder: nttImgFileId missing");
			return;
		}
		long fileId = Long.parseLong(fidStr);

		if (items == null || items.isEmpty()) {
			log.info("applyArchiveImageOrder: empty order — remove all images in group fileId={}", fileId);
			try {
				fileManageService.deleteFileGroup(fileId);
			} catch (Exception e) {
				log.warn("applyArchiveImageOrder: deleteFileGroup failed fileId={}", fileId, e);
			}
			model.put("nttImgFileId", "");
			return;
		}

		log.info("applyArchiveImageOrder: fileId={}, orderItems={}", fileId, items.size());
		List<Integer> orderedCurrentSeqs = new ArrayList<>();
		for (java.util.Map<String, Object> m : items) {
			Object typeObj = m.get("type");
			String type = typeObj != null ? typeObj.toString() : "";
			if ("existing".equals(type)) {
				Object seqObj = m.get("seq");
				if (seqObj == null) {
					log.warn("applyArchiveImageOrder: existing without seq");
					return;
				}
				orderedCurrentSeqs.add(parseJsonInt(seqObj));
			} else if ("new".equals(type)) {
				Object idxObj = m.get("index");
				if (idxObj == null) {
					log.warn("applyArchiveImageOrder: new without index");
					return;
				}
				int idx = parseJsonInt(idxObj);
				if (idx < 0 || idx >= newUploadSeqs.size()) {
					log.warn("applyArchiveImageOrder: new index out of range idx={} size={}", idx, newUploadSeqs.size());
					return;
				}
				orderedCurrentSeqs.add(newUploadSeqs.get(idx));
			} else {
				log.warn("applyArchiveImageOrder: unknown type {}", type);
				return;
			}
		}
		Set<Integer> keep = new HashSet<>(orderedCurrentSeqs);
		List<FileDTO> dbFiles = fileManageService.selectFileListByFileId(fileId);
		if (dbFiles != null) {
			for (FileDTO f : dbFiles) {
				if (f != null && !keep.contains(f.getSeq())) {
					log.info("applyArchiveImageOrder: prune fileId={} seq={}", fileId, f.getSeq());
					fileManageService.deleteFile(fileId, f.getSeq());
				}
			}
		}
		fileManageService.reorderFilesInGroup(fileId, orderedCurrentSeqs);
	}

	/**
	 * model의 기존 nttImgFileId(수정 시 폼으로 전달)가 있으면 같은 파일 그룹에 이어 붙임(게시글 첨부 articleFiles와 동일).
	 * 없으면 신규 그룹 생성. insertArtappm 다중 첨부와 같이 FileUtil.parseFileInf 사용.
	 * @return 이번에 새로 INSERT된 파일들의 SEQ(archiveImageFiles 순서와 동일). 없으면 빈 목록.
	 */
	private List<Integer> saveArchiveImageUploads(MultipartHttpServletRequest request, ModelMap model, String fileUniqId)
			throws Exception {
		List<Integer> insertedSeqs = new ArrayList<>();
		String existingNttImgStr = stringParamFirst(model.get("nttImgFileId"), model.get("NTT_IMG_FILE_ID"),
				request.getParameter("nttImgFileId"), request.getParameter("NTT_IMG_FILE_ID"));
		boolean appendToExisting = false;
		int nextSeqForAppend = 0;
		if (!existingNttImgStr.isEmpty()) {
			try {
				long fid = Long.parseLong(existingNttImgStr);
				nextSeqForAppend = fileManageService.selectFileMaxSeq(fid) + 1;
				appendToExisting = true;
			} catch (NumberFormatException e) {
				log.warn("saveArchiveImageUploads: invalid nttImgFileId, create new group. value={}", existingNttImgStr);
			}
		}

		List<MultipartFile> archiveImageFiles = request.getFiles("archiveImageFiles");
		if (archiveImageFiles != null && !archiveImageFiles.isEmpty()) {
			List<Integer> replaceSeqs = parseArchiveImageReplaceSeqs(model, request);
			boolean hasReplace = false;
			if (replaceSeqs != null) {
				for (Integer r : replaceSeqs) {
					if (r != null && r >= 0) {
						hasReplace = true;
						break;
					}
				}
			}
			if (hasReplace && appendToExisting) {
				int nextSeq = nextSeqForAppend;
				for (int i = 0; i < archiveImageFiles.size(); i++) {
					MultipartFile f = archiveImageFiles.get(i);
					String originalName = f != null ? f.getOriginalFilename() : null;
					if (f == null || f.getSize() <= 0 || originalName == null || originalName.isEmpty()) {
						continue;
					}
					int rep = (replaceSeqs != null && i < replaceSeqs.size() && replaceSeqs.get(i) != null)
							? replaceSeqs.get(i)
							: -1;
					if (rep >= 0) {
						fileManageService.updateFile(Long.parseLong(existingNttImgStr), rep, f, "FILE_", "article", null,
								null);
						continue;
					}
					Map<String, MultipartFile> single = new LinkedHashMap<>();
					single.put("archiveImageFiles_0", f);
					FileDTO imageDTO = new FileDTO();
					List<FileDTO> imageList = fileUtil.parseFileInf(single, imageDTO, existingNttImgStr, "FILE_",
							nextSeq, "article");
					if (!imageList.isEmpty()) {
						for (FileDTO fileInfo : imageList) {
							fileInfo.setUNIQ_ID(fileUniqId != null ? fileUniqId : "");
							fileManageService.insertFileInfo(fileInfo);
							insertedSeqs.add(fileInfo.getSeq());
						}
						model.put("nttImgFileId", String.valueOf(imageList.get(0).getFileId()));
						nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(existingNttImgStr)) + 1;
					}
				}
				return insertedSeqs;
			}
			Map<String, MultipartFile> imageMap = new LinkedHashMap<>();
			for (int i = 0; i < archiveImageFiles.size(); i++) {
				MultipartFile f = archiveImageFiles.get(i);
				String originalName = f != null ? f.getOriginalFilename() : null;
				if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
					imageMap.put("archiveImageFiles_" + i, f);
				}
			}
			if (!imageMap.isEmpty()) {
				FileDTO imageDTO = new FileDTO();
				List<FileDTO> imageList;
				if (appendToExisting) {
					// 기존 그룹 유지: 클라이언트 seq(0,1,2…)는 무시하고 max(seq)+1부터 연속 부여
					imageList = fileUtil.parseFileInf(imageMap, imageDTO, existingNttImgStr, "FILE_", nextSeqForAppend,
							"article");
				} else {
					List<Integer> seqs = parseArchiveImageFileSeqs(model);
					imageList = fileUtil.parseFileInf(imageMap, imageDTO, null, "FILE_", 0, "article", seqs);
				}
				if (!imageList.isEmpty()) {
					for (FileDTO fileInfo : imageList) {
						fileInfo.setUNIQ_ID(fileUniqId != null ? fileUniqId : "");
						fileManageService.insertFileInfo(fileInfo);
						insertedSeqs.add(fileInfo.getSeq());
					}
					model.put("nttImgFileId", String.valueOf(imageList.get(0).getFileId()));
				}
			}
			return insertedSeqs;
		}
		MultipartFile archiveImageFile = request.getFile("archiveImageFile");
		if (archiveImageFile != null
				&& archiveImageFile.getSize() > 0
				&& archiveImageFile.getOriginalFilename() != null
				&& !archiveImageFile.getOriginalFilename().isEmpty()) {
			Map<String, MultipartFile> imageMap = new LinkedHashMap<>();
			imageMap.put("archiveImageFile", archiveImageFile);
			FileDTO imageDTO = new FileDTO();
			List<FileDTO> imageList;
			if (appendToExisting) {
				imageList = fileUtil.parseFileInf(imageMap, imageDTO, existingNttImgStr, "FILE_", nextSeqForAppend,
						"article");
			} else {
				imageList = fileUtil.parseFileInf(imageMap, imageDTO, null, "FILE_", 0, "article");
			}
			if (!imageList.isEmpty()) {
				for (FileDTO fileInfo : imageList) {
					fileInfo.setUNIQ_ID(fileUniqId != null ? fileUniqId : "");
					fileManageService.insertFileInfo(fileInfo);
					insertedSeqs.add(fileInfo.getSeq());
				}
				model.put("nttImgFileId", String.valueOf(imageList.get(0).getFileId()));
			}
		}
		return insertedSeqs;
	}

	//게시글 목록 조회 ajax
	@ResponseBody
	@PostMapping(value = "/selectArticleList.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectArticleListAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);

		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int totalCount = articleManageService.selectArticleListCount(model);

			jsonMap.put("data", articleManageService.selectArticleList(model));
            jsonMap.put("recordsFiltered", totalCount);
            jsonMap.put("recordsTotal", totalCount);
            jsonMap.put("result", "00");
		} catch (Exception e) {
		    jsonMap.put("result", "01");
		    e.printStackTrace();
		}

        return jsonMap;
	}

	// 게시글 상세 조회 ajax (detail + atchFileId로 파일 목록 조회 후 fileList 포함)
	@ResponseBody
	@PostMapping(value = "/selectArticleDetail.Ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> selectArticleDetailAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			EgovMap detail = articleManageService.selectArticleDetail(model);
			jsonMap.put("detail", detail);
			// detail.atchFileId로 첨부파일 목록 조회 후 fileList로 담기 (fileId/seq는 문자열로 내려보내서 JS 정수 오차 방지)
			List<java.util.Map<String, Object>> fileListForJson = new java.util.ArrayList<>();
			if (detail != null) {
				Object atchFileIdObj = detail.get("atchFileId");
				if (atchFileIdObj == null) {
					atchFileIdObj = detail.get("ATCH_FILE_ID");
				}
				if (atchFileIdObj != null && !String.valueOf(atchFileIdObj).trim().isEmpty()) {
					try {
						Long atchFileId = Long.parseLong(String.valueOf(atchFileIdObj).trim());
						List<FileDTO> fileList = fileManageService.selectFileListByFileId(atchFileId);
						for (FileDTO f : fileList) {
							java.util.Map<String, Object> m = new java.util.HashMap<>();
							m.put("fileId", String.valueOf(f.getFileId()));
							m.put("seq", String.valueOf(f.getSeq()));
							m.put("orgfNm", f.getOrgfNm());
							m.put("saveNm", f.getSaveNm());
							m.put("filePath", f.getFilePath());
							m.put("fileExt", f.getFileExt());
							m.put("fileSize", Float.valueOf(f.getFileSize()));
							m.put("fileType", f.getFileType());
							m.put("sttusCode", f.getSttusCode());
							fileListForJson.add(m);
						}
					} catch (NumberFormatException e) {
						log.warn("selectArticleDetail: invalid atchFileId, skip fileList. atchFileId={}", atchFileIdObj);
					}
				}
			}
			jsonMap.put("fileList", fileListForJson);

			// 아카이브 대표 이미지 그룹(NTT_IMG_FILE_ID)에 속한 파일 목록 — 다중 이미지 미리보기용
			List<java.util.Map<String, Object>> nttImgFileListForJson = new java.util.ArrayList<>();
			if (detail != null) {
				Object nttImgFileIdObj = detail.get("nttImgFileId");
				if (nttImgFileIdObj == null) {
					nttImgFileIdObj = detail.get("NTT_IMG_FILE_ID");
				}
				if (nttImgFileIdObj != null && !String.valueOf(nttImgFileIdObj).trim().isEmpty()) {
					try {
						Long nttImgFid = Long.parseLong(String.valueOf(nttImgFileIdObj).trim());
						List<FileDTO> nttImgFiles = fileManageService.selectFileListByFileId(nttImgFid);
						for (FileDTO f : nttImgFiles) {
							java.util.Map<String, Object> m = new java.util.HashMap<>();
							m.put("fileId", String.valueOf(f.getFileId()));
							m.put("seq", String.valueOf(f.getSeq()));
							m.put("orgfNm", f.getOrgfNm());
							m.put("saveNm", f.getSaveNm());
							m.put("filePath", f.getFilePath());
							m.put("fileExt", f.getFileExt());
							m.put("fileSize", Float.valueOf(f.getFileSize()));
							m.put("fileType", f.getFileType());
							nttImgFileListForJson.add(m);
						}
					} catch (NumberFormatException e) {
						log.warn("selectArticleDetail: invalid nttImgFileId, skip nttImgFileList. id={}", nttImgFileIdObj);
					}
				}
			}
			jsonMap.put("nttImgFileList", nttImgFileListForJson);

			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	/** 게시글 첨부파일 1건 삭제 (fileId+seq). 삭제 후 해당 fileId에 남은 파일이 없으면 ARTBBSM.ATCH_FILE_ID를 NULL로 갱신 */
	@ResponseBody
	@PostMapping(value = "/deleteArticleFile.Ajax", produces = "application/json;charset=UTF-8")
	public HashMap<String, Object> deleteArticleFileAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();

		try {
			String fileIdStr = model.get("fileId") != null ? model.get("fileId").toString().trim() : "";
			String seqStr = model.get("seq") != null ? model.get("seq").toString().trim() : "";
			String bbsId = model.get("bbsId") != null ? model.get("bbsId").toString().trim() : "";
			String nttId = model.get("nttId") != null ? model.get("nttId").toString().trim() : "";

			if (fileIdStr.isEmpty() || seqStr.isEmpty()) {
				jsonMap.put("result", "01");
				jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
				return jsonMap;
			}

			Long fileId = Long.parseLong(fileIdStr);
			Integer seq = Integer.parseInt(seqStr);
			log.info("deleteArticleFile.Ajax received - fileId: {}, seq: {}, bbsId: {}, nttId: {}", fileId, seq, bbsId, nttId);

			fileManageService.deleteFile(fileId, seq);

			List<FileDTO> remaining = fileManageService.selectFileListByFileId(fileId);
			if (remaining.isEmpty() && !bbsId.isEmpty() && !nttId.isEmpty()) {
				model.put("type", "atchFileId");
				model.put("bbsId", bbsId);
				model.put("nttId", nttId);
				if (model.get("UNIQ_ID") == null || model.get("UNIQ_ID").toString().isEmpty()) {
					HttpSession session = request.getSession();
					LoginVO user = session != null ? (LoginVO) session.getAttribute("user") : null;
					model.put("UNIQ_ID", user != null && user.getUniqId() != null ? user.getUniqId() : "");
				}
				articleManageService.deleteArticleMultiFile(model);
			}

			jsonMap.put("result", "00");
			jsonMap.put("message", egovMessageSource.getMessage("success.common.delete"));
		} catch (NumberFormatException e) {
			log.warn("deleteArticleFile: invalid fileId or seq", e);
			jsonMap.put("result", "01");
			jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
		} catch (Exception e) {
			log.error("deleteArticleFile error", e);
			jsonMap.put("result", "01");
			jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
		}

		return jsonMap;
	}

	// 게시글 등록 ajax (간단한 버전 - 파일 업로드 제외)
	@ResponseBody
    @PostMapping(value = "/insertArticle.Ajax", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> insertArticleAjax(MultipartHttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);
        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
			// ntcrStartDt/ntcrEndDt 미입력 시 기본값: 등록 시점 ~ 9999-12-31 00:00:00
			Object ntcrStart = model.get("ntcrStartDt");
			if (ntcrStart == null || ntcrStart.toString().trim().isEmpty()) {
				model.put("ntcrStartDt", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
			}
			Object ntcrEnd = model.get("ntcrEndDt");
			if (ntcrEnd == null || ntcrEnd.toString().trim().isEmpty()) {
				model.put("ntcrEndDt", "9999-12-31 00:00:00");
			}

			String answerAt    = "";
			int newNttId = articleManageService.getNextNttId(model);
			if( model.get("answerAt") != null ) {
				answerAt    = model.get("answerAt").toString();
			}

			// [1] 비밀번호를 입력한 경우에만 암호화
			String newPw = "";
			if( model.get("password") != null && !"".equals(model.get("password").toString())) {
				newPw  = EgovFileScrty.encryptPassword(model.get("password").toString(), "");
			}
			// [2] 모델 주입
			model.put("newPw" , newPw );
			
			
			model.put("nttCnFileId", "");
			model.put("atchFileId", "");

			// 파일 저장 시 CHG_USER_ID용 uniqId (프론트/세션에서 먼저 조회)
			String fileUniqId = model.get("uniqId") != null ? model.get("uniqId").toString().trim() : "";
			if (fileUniqId.isEmpty()) {
				HttpSession sessionForFile = request.getSession();
				LoginVO userForFile = sessionForFile != null ? (LoginVO) sessionForFile.getAttribute("user") : null;
				if (userForFile != null && userForFile.getUniqId() != null) {
					fileUniqId = userForFile.getUniqId();
				}
			}

			// 첨부파일 저장 (common/files): 파일이 있으면 저장 후 fileId를 atchFileId에 설정
			List<MultipartFile> multipartList = request.getFiles("articleFiles");
			if (multipartList != null && !multipartList.isEmpty()) {
				Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
				for (int i = 0; i < multipartList.size(); i++) {
					MultipartFile f = multipartList.get(i);
					String originalName = f != null ? f.getOriginalFilename() : null;
					if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
						fileMap.put("articleFiles_" + i, f);
					}
				}
				if (!fileMap.isEmpty()) {
					FileDTO fileDTO = new FileDTO();
					List<FileDTO> fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "article");
					if (!fileList.isEmpty()) {
						for (FileDTO fileInfo : fileList) {
							fileInfo.setUNIQ_ID(fileUniqId != null ? fileUniqId : "");
							fileManageService.insertFileInfo(fileInfo);
						}
						model.put("atchFileId", String.valueOf(fileList.get(0).getFileId()));
					}
				}
			}

			// 아카이브 이미지 다건(archiveImageFiles + archiveImageFileSeqs) 또는 단건(archiveImageFile)
			List<Integer> newArchiveSeqs = saveArchiveImageUploads(request, model, fileUniqId);
			applyArchiveImageOrder(request, model, newArchiveSeqs);

			// ▼ 업데이트 전
			if(model.get("boardInfo1") != null) model.put("nttCn", StringEscapeUtils.unescapeHtml4(model.get("boardInfo1").toString()));

			// 사용자 정보 설정 (프론트엔드에서 보낸 값 우선, 없으면 세션에서 가져오기)
			String uniqId = null;
			String name = null;
			
			// 프론트엔드에서 보낸 값 확인
			if(model.get("uniqId") != null) {
				uniqId = model.get("uniqId").toString();
				log.info("프론트엔드에서 받은 uniqId: " + uniqId);
			}
			if(model.get("name") != null) {
				name = model.get("name").toString();
				log.info("프론트엔드에서 받은 name: " + name);
			}
			
			// 프론트엔드에서 보낸 값이 세션에서 가져오기
			if(uniqId == null || uniqId.isEmpty()) {
				HttpSession session = request.getSession();
				LoginVO user = (LoginVO) session.getAttribute("user");
				if(user != null && user.getUniqId() != null) {
					uniqId = user.getUniqId();
					log.info("세션에서 가져온 uniqId: " + uniqId);
				} else {
					log.warn("세션에서 user 정보를 가져올 수 없습니다.");
				}
			}
			if(name == null || name.isEmpty()) {
				HttpSession session = request.getSession();
				LoginVO user = (LoginVO) session.getAttribute("user");
				if(user != null && user.getName() != null) {
					name = user.getName();
					log.info("세션에서 가져온 name: " + name);
				} else {
					log.warn("세션에서 user name을 가져올 수 없습니다.");
				}
			}
			
			// model에 설정 (SQL 매퍼에서 UNIQ_ID를 사용하므로 일치시킴)
			if(uniqId != null && !uniqId.isEmpty()) {
				model.put("UNIQ_ID", uniqId); // SQL 매퍼에서 #{UNIQ_ID} 사용
				model.put("ntcrId", uniqId); // 혹시 다른 곳에서 사용할 수 있으므로 둘 다 설정
				model.put("frstRegisterId", uniqId);
				log.info("model에 설정된 UNIQ_ID: " + model.get("UNIQ_ID"));
			} else {
				log.error("uniqId가 설정되지 않았습니다!");
			}
			if(name != null && !name.isEmpty()) {
				model.put("ntcrNm", name);
				log.info("model에 설정된 ntcrNm: " + model.get("ntcrNm"));
			} else {
				log.error("name이 설정되지 않았습니다!");
			}

			// ▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩
			// ▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩ 답글처리 (순창웹페이지) ▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩
			// ▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩
			// --------------------------
			// ▼ [1] 답글형인 경우에 실행하기 (answerAt이 "Y"이고 nttId가 존재할 때만)
			// ---------------------------
			if( answerAt.equals("Y") && model.get("nttId") != null ) {
				model.put("parntscttId"    , model.get("nttId").toString());                           // 화면의기존NTTID 설정
				model.put("nttId"          , newNttId);                                                // 후에 nttId 새로 셋팅
				model.put("answerAt"       , "Y");                                                     // 답글게시물로 설정
				//model.put("answerLc"       , Integer.parseInt(model.get("answerLc").toString()) + 1 ); // 답글순서
				//model.put("sortOrdr"       , Long.parseLong(model.get("sortOrdr").toString()));        // 부모글
				//model.put("frstRegisterId" , model.get("UNIQ_ID").toString());                         // 관리자
				//model.put("lastUpdusrId"   , model.get("lastUpdusrId").toString());                    // 부모글
				//model.put("ntcrId"   	   , model.get("UNIQ_ID").toString());
				//model.put("ntcrNm"   	   , model.get("LOGIN_NM").toString());
				jsonMap.put("data", articleManageService.replyArticle(model));
			// --------------------------
			// ▼ [2] 답글형이 아니면 실행하기 (일반 게시글 등록)
			// ---------------------------
			}else {
				model.put("nttId"       , newNttId);
				int data = articleManageService.insertArticle(model);
				jsonMap.put("data" , data);
			}

			jsonMap.put("result", "00");
            jsonMap.put("message", egovMessageSource.getMessage("success.common.insert"));
		} catch (BusinessException e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", e.getMessage());
			log.warn("Article insert file error: {}", e.getMessage());
		} catch (Exception e) {
			jsonMap.put("result", "01");
		    jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
			e.printStackTrace();
		}

        return jsonMap;
    }

	// 게시글 수정 ajax (파일 업로드 포함: 새 파일 추가 시 기존 atchFileId에 append 또는 신규 fileId 생성)
	@ResponseBody
	@PostMapping(value = "/updateArticle.ajax", produces = "application/json;charset=UTF-8")
	public HashMap<String, Object> updateArticleAjax(MultipartHttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();

		try {
			// 본문 HTML (boardInfo1 -> nttCn)
			if (model.get("boardInfo1") != null) {
				model.put("nttCn", StringEscapeUtils.unescapeHtml4(model.get("boardInfo1").toString()));
			}

			// 파일/게시글 CHG_USER_ID용 uniqId (프론트 전달값 우선, 없으면 세션)
			String uniqIdForUpdate = model.get("uniqId") != null ? model.get("uniqId").toString().trim() : "";
			if (uniqIdForUpdate.isEmpty()) {
				HttpSession sessionForUpdate = request.getSession();
				LoginVO userForUpdate = sessionForUpdate != null ? (LoginVO) sessionForUpdate.getAttribute("user") : null;
				if (userForUpdate != null && userForUpdate.getUniqId() != null) {
					uniqIdForUpdate = userForUpdate.getUniqId();
				}
			}
			if (!uniqIdForUpdate.isEmpty()) {
				model.put("UNIQ_ID", uniqIdForUpdate);
			}

			// 첨부파일 저장: articleFiles가 있으면 기존 atchFileId에 추가하거나 신규 fileId 생성 후 model에 반영
			String atchFileIdStr = model.get("atchFileId") != null ? model.get("atchFileId").toString().trim() : "";
			List<MultipartFile> multipartList = request.getFiles("articleFiles");
			if (multipartList != null && !multipartList.isEmpty()) {
				Map<String, MultipartFile> fileMap = new LinkedHashMap<>();
				for (int i = 0; i < multipartList.size(); i++) {
					MultipartFile f = multipartList.get(i);
					String originalName = f != null ? f.getOriginalFilename() : null;
					if (f != null && f.getSize() > 0 && originalName != null && !originalName.isEmpty()) {
						fileMap.put("articleFiles_" + i, f);
					}
				}
				if (!fileMap.isEmpty()) {
					FileDTO fileDTO = new FileDTO();
					List<FileDTO> fileList;
					if (atchFileIdStr.isEmpty()) {
						// 기존 첨부 없음 → 새 fileId 생성
						fileList = fileUtil.parseFileInf(fileMap, fileDTO, null, "FILE_", 0, "article");
						if (!fileList.isEmpty()) {
							for (FileDTO fileInfo : fileList) {
								fileInfo.setUNIQ_ID(uniqIdForUpdate != null ? uniqIdForUpdate : "");
								fileManageService.insertFileInfo(fileInfo);
							}
							model.put("atchFileId", String.valueOf(fileList.get(0).getFileId()));
						}
					} else {
						// 기존 atchFileId에 파일 추가
						int nextSeq = fileManageService.selectFileMaxSeq(Long.parseLong(atchFileIdStr)) + 1;
						fileList = fileUtil.parseFileInf(fileMap, fileDTO, atchFileIdStr, "FILE_", nextSeq, "article");
						if (!fileList.isEmpty()) {
							for (FileDTO fileInfo : fileList) {
								fileInfo.setUNIQ_ID(uniqIdForUpdate != null ? uniqIdForUpdate : "");
								fileManageService.insertFileInfo(fileInfo);
							}
						}
					}
				}
			}

			// 아카이브 이미지 다건 또는 단건 (신규 업로드 시에만 model.nttImgFileId 갱신)
			List<Integer> newArchiveSeqs = saveArchiveImageUploads(request, model,
					uniqIdForUpdate != null ? uniqIdForUpdate : "");
			applyArchiveImageOrder(request, model, newArchiveSeqs);

			// 비밀글 비밀번호 변경 시 암호화하여 newPw로 전달 (수정 시에만 반영)
			if (model.get("password") != null && !model.get("password").toString().trim().isEmpty()) {
				String newPw = EgovFileScrty.encryptPassword(model.get("password").toString().trim(), "");
				model.put("newPw", newPw);
			}

			int result = articleManageService.updateArticle(model);
			jsonMap.put("data", result);
			jsonMap.put("result", "00");
			jsonMap.put("message", egovMessageSource.getMessage("success.common.update"));
		} catch (BusinessException e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", e.getMessage());
			log.warn("Article update file error: {}", e.getMessage());
		} catch (Exception e) {
			jsonMap.put("result", "01");
			jsonMap.put("message", egovMessageSource.getMessage("fail.common.msg"));
			log.error("Article update error", e);
		}

		return jsonMap;
	}

	// 게시글 삭제 ajax
	@ResponseBody
	@PostMapping(value = "/deleteArticle.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> deleteArticleAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			// CHG_USER_ID용: 로그인 사용자 uniqId를 UNIQ_ID로 설정 (프론트 전달값 우선, 없으면 세션)
			String uniqId = model.get("uniqId") != null ? model.get("uniqId").toString().trim() : "";
			if (uniqId.isEmpty()) {
				HttpSession session = request.getSession();
				LoginVO user = session != null ? (LoginVO) session.getAttribute("user") : null;
				if (user != null && user.getUniqId() != null) {
					uniqId = user.getUniqId();
				}
			}
			if (!uniqId.isEmpty()) {
				model.put("UNIQ_ID", uniqId);
			}

			int result = articleManageService.deleteArticle(model);
			jsonMap.put("data", result);
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	// 조회수 증가
	@ResponseBody
	@PostMapping(value = "/updateViewCount.ajax", produces="application/json;charset=UTF-8")
	public HashMap<String, Object> updateViewCountAjax(HttpServletRequest request, ModelMap model) throws Exception {
		this.setCommon(request, model);
		HashMap<String, Object> jsonMap = new HashMap<String,Object>();

		try {
			int result = articleManageService.updateViewCount(model);
			jsonMap.put("data", result);
			jsonMap.put("result", "00");
		} catch (Exception e) {
			jsonMap.put("result", "01");
			e.printStackTrace();
		}

		return jsonMap;
	}

	/*
	 * 주석: 아래 메서드들은 arami.v3 패키지의 파일 업로드 관련 클래스들이 필요합니다.
	 * 해당 패키지가 제공되면 주석을 해제하고 사용하세요.
	 *
	 * - insertArticleAjax (파일 업로드 포함)
	 * - updateArticleAjax (파일 업로드 포함)
	 * - deleteArticleMultiFileAjax
	 * - 기타 파일 관련 메서드들
	 */
}
