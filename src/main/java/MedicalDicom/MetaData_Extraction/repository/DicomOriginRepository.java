package MedicalDicom.MetaData_Extraction.repository;
import MedicalDicom.MetaData_Extraction.entity.DicomOriginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DicomOriginRepository extends JpaRepository<DicomOriginEntity, Long>
{
    @Query("SELECT d FROM DicomOriginEntity d " +
           "WHERE (:isT1Axial IS NULL OR d.isT1Axial = :isT1Axial) " +
           "AND (:modalities IS NULL OR d.modality IN :modalities) " +
           "AND (:minAge IS NULL OR d.patientAge >= :minAge) " +
           "AND (:maxAge IS NULL OR d.patientAge <= :maxAge) " +
           "ORDER BY d.instanceNumber ASC")

    List<DicomOriginEntity> findByFilters(
            @Param("isT1Axial") Boolean isT1Axial,
            @Param("modalities") List<String> modalities,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge
    );
}
