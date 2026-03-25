package MedicalDicom.MetaData_Extraction.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dicom_origin")
@Setter
@Getter
public class DicomOriginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String patientName;

    @Column(length = 50, nullable = false)
    private String modality;

    @Column(length = 500)
    private String originFilePath;

    @Column(nullable = false)
    private boolean isT1Axial = false;

    @Column(nullable = true) //디폴트 값
    private Integer patientAge;

    @Column(nullable = false)
    private Integer instanceNumber;

    @OneToMany(mappedBy = "originDicom",cascade = CascadeType.ALL)
    private List<DicomConversionEntity> conversions = new ArrayList<>();
}
