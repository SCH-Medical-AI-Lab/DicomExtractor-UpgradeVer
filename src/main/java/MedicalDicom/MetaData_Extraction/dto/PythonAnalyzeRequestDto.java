package MedicalDicom.MetaData_Extraction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PythonAnalyzeRequestDto {

    @JsonProperty("input_dicom_file_path")
    private String inputDicomFilePath;

    @JsonProperty("output_png_file_path")
    private String outputPngFilePath; // 변환된 파일이 FastAPI로부터 다시 돌아와야 함.
}
