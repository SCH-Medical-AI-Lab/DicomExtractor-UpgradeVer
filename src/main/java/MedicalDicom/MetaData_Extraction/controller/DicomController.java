package MedicalDicom.MetaData_Extraction.controller;

import MedicalDicom.MetaData_Extraction.dto.DicomSearchResultDto;
import MedicalDicom.MetaData_Extraction.entity.DicomOriginEntity;
import MedicalDicom.MetaData_Extraction.service.DicomExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dicom")
@RequiredArgsConstructor
public class DicomController {
    private final DicomExtractorService dicomService;


    // 업로드
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

        /*TODO: Service 계층에서 .zip 파일을 특정 폴더에 압축 해제하는 메서드를 호출하고, file을 넘겨주어야 함.*/

        try {
            dicomService.processZipFile(file);
            return ResponseEntity.ok("압축 파일 업로드 및 분석, DB저장 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리 중 오류 발생: " + e.getMessage());
        }

        // 나머지 검색 API 생략
    }

    // 처리 중 오류 발생: D:\Temp_Dir\55de613f-132d-4edc-b932-d25da30330bf_02ef8f31ea86a45cfce6eb297c274598MR\series-000001


    // 다중 필터 검색 (체크박스 대응)
    @GetMapping("/search")
    public ResponseEntity<?> searchHistory(
            @RequestParam(value = "isT1Axial", required = false) Boolean isT1Axial,
            @RequestParam(value = "modalities", required = false)List<String> modalities, //모달리틔는 여러개 선택이 가능하므로
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge) {


        // TODO: DicomService (서비스 계층)에서 dicomOriginRepository.findByFilters(...) 호출 후 결과 반환
        List<DicomSearchResultDto> results = dicomService.searchHistory(isT1Axial, modalities,minAge,maxAge);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<?> searchDetailHistory(@PathVariable Long id){
        try{
            DicomOriginEntity detail = dicomService.getDetail(id);

            if(detail != null) {
                return ResponseEntity.ok(detail);
            } else {
                return ResponseEntity.status(404).body("해당 아이디의 데이터를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // 변환 실행
    @PostMapping("/convert/{id}")
    public ResponseEntity<?> convertFile(@PathVariable("id") Long id) {
        try{
            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("status", "PROCESSING");
            response.put("message","Conversion started");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("FAILED : CausedBy" + e.getMessage());
        }

    }

    // 다운로드
//    @GetMapping("/download/{id}")
//    public ResponseEntity<?> downLoadConversionFile(@PathVariable Long id) {
//
//        try {
//            // 서비스에서 DCM 파일을 변환하는 로직을 불러와야 함.
//            return ResponseEntity.ok();
//        } catch (Exception e) {
//
//        }
//    }
}
