package MedicalDicom.MetaData_Extraction.controller;

import MedicalDicom.MetaData_Extraction.service.DicomExtractorService;
import lombok.RequiredArgsConstructor;
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

        /*TODO: Service 계층에서 .zip 파일을 특정 폴더에 압축 해제하고
        /폴더 내의 각 DICOM 파일들을 순회하며 Python 서버에 분석 요청을 보내는 로직이 필요*/

        return ResponseEntity.ok("압축 파일 업로드 및 분석 시작.");
    }


    // 다중 필터 검색 (체크박스 대응)
    @GetMapping("/search")
    public ResponseEntity<?> searchHistory(
            @RequestParam(value = "isT1Axial", required = false) Boolean isT1Axial,
            @RequestParam(value = "modalities", required = false)List<String> modalities, //모달리틔는 여러개 선택이 가능하므로
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge) {


        // TODO: DicomService (서비스 계층)에서 dicomOriginRepository.findByFilters(...) 호출 후 결과 반환
        return ResponseEntity.ok("검색이 완료되었습니다.");
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<?> searchDetailHistory(@PathVariable Long id){
        try{

            if () {
                return
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    // 변환 실행
    @PostMapping("/convert")
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
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downLoadConversionFile(@PathVariable Long id) {

        try {
            // 서비스에서 DCM 파일을 변환하는 로직을 불러와야 함.

        } catch (Exception e) {

        }
    }
}
