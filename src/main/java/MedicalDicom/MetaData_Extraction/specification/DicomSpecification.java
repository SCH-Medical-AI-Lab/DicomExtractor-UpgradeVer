package MedicalDicom.MetaData_Extraction.specification;

import MedicalDicom.MetaData_Extraction.entity.DicomOriginEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DicomSpecification {

    // Modality 다중검색
    // 사용자가 체크한 모달리티 리스트 ["CT", "MR"] 중 하나라도 일치하면 찾아옴.
    public static Specification<DicomOriginEntity> hasModalityIn(List<String> modalities) {
        return (root, query, criteriaBuilder) -> {
            if (modalities == null || modalities.isEmpty()) {
                return null;
            }
            // SQL : WHERE modality In ('CT', 'MR')
            return root.get("modality").in(modalities);
        };
    }

    public static Specification<DicomOriginEntity> ageBetween(Integer minAge, Integer maxAge) {
        return ((root, query, criteriaBuilder) -> {
            if (minAge == null && maxAge == null) return null;

            // 최소 나이와 최대 나이가 모두 있을 때 (BETWEEN)
            if (minAge != null && maxAge != null) {
                return criteriaBuilder.between(root.get("patientAge"), minAge, maxAge);
            }

            //최소 나이만 있을 때 (minAge 이상)
            if (minAge != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("patientAge"), minAge);
            }

            // 최대 나이만 있을 때 (maxAge 이하)
            return criteriaBuilder.lessThanOrEqualTo(root.get("patientAge"), maxAge);
        });
    }

    public static Specification<DicomOriginEntity> isT1Axial(Boolean isT1Axial) {
        return (root, query, criteriaBuilder) -> {
            // 체크 박스를 안 눌렀으면 이 조건은 무시
            if (isT1Axial == null || !isT1Axial) {
                return null;
            }

            // SQL : WHERE isT1Axial = true
            // 엔티티의 boolean 값이 true인지 확인합니다.
            return criteriaBuilder.isTrue(root.get("isT1Axial"));
        };
    }

}

