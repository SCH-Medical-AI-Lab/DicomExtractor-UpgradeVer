package MedicalDicom.MetaData_Extraction.controller;

import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeRequestDto;
import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeResponseDto;
import MedicalDicom.MetaData_Extraction.service.PythonIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final PythonIntegrationService pythonIntegrationService;

    @GetMapping
    public PythonAnalyzeResponseDto testPythonCall() {
        return pythonIntegrationService.getDummyAnalysis();
    }
}
