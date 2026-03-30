package MedicalDicom.MetaData_Extraction.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DicomSearchResultDto {

    //프론트엔드가 화면에 사진을 띄우는 데 필요한 핵심 정보만 담음.

    private Long id;
    private String patientName;
    private Integer patientAge;
    private String modality;
    private boolean isT1Axial;

    private List<String> pngImagePaths;
}
