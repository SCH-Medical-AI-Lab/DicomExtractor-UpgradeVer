package MedicalDicom.MetaData_Extraction.repository;

import MedicalDicom.MetaData_Extraction.entity.DicomConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DicomConversionRepository extends JpaRepository<DicomConversionEntity,Long> {

}
