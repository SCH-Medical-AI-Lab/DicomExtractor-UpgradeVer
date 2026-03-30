package MedicalDicom.MetaData_Extraction.service;

import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeRequestDto;
import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

//Python (FastAPI)와 통신을 직접적으로 하는 서비스 부분
//파이썬 서버의 문을 두드리는(HTTP GET 요청) 핵심 역할을 합니다.
@Service
public class PythonIntegrationService {

    private final RestClient restClient;

    // Python(Fast_API)와 연결됨.
    // 1. 파이썬 서버(8000번 포트)를 목적지로 하는 통신 객체를 조립합니다.
    public PythonIntegrationService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://127.0.0.1:8000")
                .build();
    }

    // 2. 파이썬에 통신을 걸어서 결과를 받아옴
    public PythonAnalyzeResponseDto requestAnalysis(PythonAnalyzeRequestDto requestDto) {
        return restClient.post()             // GET 방식으로 부름 (post, put, patch, delete.... 다 있음.)
                .uri("/analyze/dicom")  // 경로는 다음과 같으며 (파이썬 FastAPI의 실제 분석 엔드포인트 --> 가정)
                .body(requestDto)
                .contentType(MediaType.APPLICATION_JSON) //Json형태로 보낸다고 명시. 왜? 명시를 해야 하나?
                // 여기서 분리됨.
                .retrieve()                 // 결과를 가져와서...
                .body(PythonAnalyzeResponseDto.class);   // 내가 만든 자바 바구니 (DTO)에 담는다.
    }


}
