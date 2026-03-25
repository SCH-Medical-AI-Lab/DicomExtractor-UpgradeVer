package MedicalDicom.MetaData_Extraction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PythonAnalyzeResponseDto {
    // DTO 객체를 Jackson 라이브러리 등을 통해 JSON 텍스트로 직렬화하여 네트워크로 전송합니다.

    private String status;

    @JsonProperty("is_t1_axial")
    private boolean isT1Axial;

    @JsonProperty("patient_name")
    private String patientName;

    private String modality;

    @JsonProperty("patient_age")
    private Integer patientAge;

    @JsonProperty("instance_number") //사진 정렬 시 필요한 번호. 즉 정렬 번호
    private Integer instanceNumber;

    @JsonProperty("output_png_file_path")
    private String outputPngFilePath;
}
