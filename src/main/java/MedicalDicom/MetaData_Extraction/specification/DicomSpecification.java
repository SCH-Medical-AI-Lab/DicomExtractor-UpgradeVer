package MedicalDicom.MetaData_Extraction.specification;

import MedicalDicom.MetaData_Extraction.entity.DicomOriginEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DicomSpecification {

    public static Specification<DicomOriginEntity> fetchConversions() {
        return (root, query, criteriaBuilder) -> {
            // 중요: count 쿼리(데이터 개수 세기)가 나갈 때는 fetch join을 하면 에러가 난다.
            // SELECT COUNT(d) FROM DicomOriginEntity d LEFT JOIN FETCH d.conversions <-- queryException터짐.
            // fetch join은 row가 늘어나서 count 의미가 깨져서 그렇다.
            // 그래서 결과 타입이 '부모 엔티티'일 때만 fetch를 수행하도록 방어 코드를 넣는다.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
              root.fetch("conversions", JoinType.LEFT); //conversions(자식) 리스트를 미리 가져오기
          }
          return null; // 조건(WHERE) 필터링이 아니라 가져오는 방식(JOIN)만 바꾸는 것이라 null 반환
        };
    }

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
            // 엔티티의 boolean 값이 true인지 확인.
            return criteriaBuilder.isTrue(root.get("isT1Axial"));
        };
    }

}

