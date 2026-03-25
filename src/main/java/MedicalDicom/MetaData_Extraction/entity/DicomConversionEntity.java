package MedicalDicom.MetaData_Extraction.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dicom_conversion")
@Getter
@Setter
public class DicomConversionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String convertedFilePath;

    @Column(nullable = false)
    private LocalDateTime conversionDate;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id",
                foreignKey = @ForeignKey(name = "fk_conversion_to_origin"))
    private DicomOriginEntity originDicom;
}
