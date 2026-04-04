package MedicalDicom.MetaData_Extraction.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore //Json으로 바꿀 때는 이 변수는 무시하고 쳐다보지 말라고 해야 한다. 그렇지 않으면 자식이 부모를 보고 다시 부모가 자식을 보는 순환참조가 일어난다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id",
                foreignKey = @ForeignKey(name = "fk_conversion_to_origin")) // 이렇게 안하면 외래키를 랜덤문자로 짜버림. 외래키의 기능을 알 수 없게 됨.
    private DicomOriginEntity originDicom;
}
