package MedicalDicom.MetaData_Extraction.service;

import MedicalDicom.MetaData_Extraction.dto.DicomSearchResultDto;
import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeRequestDto;
import MedicalDicom.MetaData_Extraction.dto.PythonAnalyzeResponseDto;
import MedicalDicom.MetaData_Extraction.entity.DicomConversionEntity;
import MedicalDicom.MetaData_Extraction.entity.DicomOriginEntity;
import MedicalDicom.MetaData_Extraction.repository.DicomConversionRepository;
import MedicalDicom.MetaData_Extraction.repository.DicomOriginRepository;
import MedicalDicom.MetaData_Extraction.specification.DicomSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DicomExtractorService {
    private final DicomOriginRepository dicomOriginRepository;
    private final DicomConversionRepository dicomConversionRepository;
    private final PythonIntegrationService pythonIntegrationService;

    private final String Temp_Dir = "D://Temp_Dir";


    // 압축 파일을 받아서 처리하는 메인 메서드 여기서 수만 개의 파일 중 하나라도 실패하면 전체 취소되어야 함
    // ZIP 파일도 MultipartFile 파일의 확장자나 종류(JPG, PNG, PDF, ZIP 등)와
    // 상관없이 HTTP 프로토콜을 통해 업로드되는 파일 데이터를 서버에서 다루는 방식
    @Transactional
    public void processZipFile(MultipartFile multipartFile) throws Exception{

        // 임시폴더가 없으면 만듦. 폴더를 만들려면 경로와 파일 객체가 필요하다.
        Path tempDirPath = Paths.get(Temp_Dir);
        if(!Files.exists(tempDirPath)) {
            Files.createDirectories(tempDirPath);
        }

        // 압축파일을 받아서 압축 파일 내부의 파일이 더 이상 나오지 않을 때까지 확인하며 도는 것.
        try (ZipInputStream zipInputStream = new ZipInputStream(multipartFile.getInputStream())) {
            ZipEntry zipEntry; //ZipEntry는 파일 내의 각 개별 파일(엔트리)을 나타냄

            // 각 개별 파일이 null 이 아닌지 getNextEntry()로 ZipEntry객체를 돌면서 확인함. 기억할 것.
            while((zipEntry = zipInputStream.getNextEntry())!= null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().toLowerCase().endsWith("dcm")) {
                    log.info("DICOM 파일 발견: " + zipEntry.getName());

                    /// 저장할 파일의 전체 경로 생성 (예: D:/Temp_Dir/001.dcm)
                    Path targetPath = Paths.get(Temp_Dir, zipEntry.getName());

                    ///  저장할 파일의 전체 경로를 가지고 만들어 주어야 한다. 밑의 Files.copy()는 이미 만들어진 경로에 저장 혹은 복사만 하는 풀이 좁은 메서드
                    if (targetPath.getParent() != null && !Files.exists(targetPath.getParent())) {
                        Files.createDirectories(targetPath.getParent());
                    }

                    //StandardCopyOption이 CopyOption이다. 옵션은 3개가 있다.
                    //REPLACE_EXISTING: 이미 파일이 존재하면 덮어쓰기
                    //COPY_ATTRIBUTES: 파일의 속성(권한, 수정일 등)도 같이 복사
                    //ATOMIC_MOVE: 파일을 원자적으로 이동 (copy에서는 쓰지 않고 move에서 주로 사용)

                    //zipInputStream에서 나오는 데이터를 targetPath(목적지)에 파일로 저장(복사)해야 함. 그것을 하는 함수가 Files.copy이다.
                    Files.copy(zipInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("파일 임시저장 완료"); // 저장은 이걸로 끝난다.

                    //파이썬이 변환을 마치고 저장할 PNG파일 경로도 미리 정해줌. 경로를 완성시키는 작업 -> 파이썬은 작업을 마치고 이 경로로 저장을 시도할 것이다.
                    String pngPath = String.valueOf(Paths.get(Temp_Dir, zipEntry.getName().replace(".dcm", ".png")));

                    //이제 DTO에 넣어서 보내보자
                    PythonAnalyzeRequestDto requestDto = new PythonAnalyzeRequestDto(
                            targetPath.toString(),
                            pngPath
                    );

                    // Python 서버에 요청 보내고 응답 받기 (Mission 1이 해결되었다고 가정)
                    PythonAnalyzeResponseDto pythonAnalyzeResponseDto = pythonIntegrationService.requestAnalysis(requestDto);
                    if (pythonAnalyzeResponseDto.getStatus().equals("SUCCESS")) {

                        //원본 (Origin) 엔티티 조립
                        DicomOriginEntity originEntity = new DicomOriginEntity();
                        originEntity.setInstanceNumber(pythonAnalyzeResponseDto.getInstanceNumber());
                        originEntity.setT1Axial(pythonAnalyzeResponseDto.isT1Axial());
                        originEntity.setModality(pythonAnalyzeResponseDto.getModality());
                        originEntity.setPatientName(pythonAnalyzeResponseDto.getPatientName());
                        originEntity.setPatientAge(pythonAnalyzeResponseDto.getPatientAge());
                        originEntity.setOriginFilePath(pythonAnalyzeResponseDto.getOutputPngFilePath());

                        //변환 엔티티 조립
                        DicomConversionEntity conversionEntity = new DicomConversionEntity();
                        conversionEntity.setConversionDate(LocalDateTime.now());
                        conversionEntity.setConvertedFilePath(pythonAnalyzeResponseDto.getOutputPngFilePath());
                        conversionEntity.setStatus(pythonAnalyzeResponseDto.getStatus());

                        // 가장 중요한 부분 ==> JPA에게 이 변환된 이미지(ConversionEntity)는 저 원본(OriginEntity)에서 나온 것이라고
                        // 두 객체의 '관계'를 맺어주어야 합니다.
                        conversionEntity.setOriginDicom(originEntity);

                        dicomOriginRepository.save(originEntity);
                        dicomConversionRepository.save(conversionEntity);

                        log.info("DB 저장 완료. 환자 이름: {}",originEntity.getPatientName());
                    }

                }
            }
        }
    }


    public List<DicomSearchResultDto> searchHistory(Boolean isT1Axial, List<String> modalities, Integer minAge, Integer maxAge) {

        // 1. 조립하기 (Specification 엮기)
        // 조건이 비어있으면 자동으로 해당 조건은 무시하고 쿼리를 똑똑하게 조립함.
        Specification<DicomOriginEntity> spec = Specification.where(DicomSpecification.fetchConversions())
                .and(DicomSpecification.isT1Axial(isT1Axial))
                .and(DicomSpecification.hasModalityIn(modalities))
                .and(DicomSpecification.ageBetween(minAge, maxAge));

        // 2. 조립된 쿼리로 DB에서 부모(Origin) 데이터 검색!
        List<DicomOriginEntity> origins = dicomOriginRepository.findAll(spec);

        // 3. 검색된 엔티티들을 프론트엔드에 보낼 안전한 DTO 상자로 변환. 이렇게 안하면 순환참조 일어나서 WriteException일어남
        // 즉 방법이 2가지이다. @JsonIgnore을 하던지, DTO를 만들어서 여기에 넣던지. 그런데 보통 후자가 선호된다.
        List<DicomSearchResultDto> resultList = new ArrayList<>();

        for (DicomOriginEntity origin : origins) {
            DicomSearchResultDto dicomSearchResultDto = new DicomSearchResultDto();

            dicomSearchResultDto.setId(origin.getId());
            dicomSearchResultDto.setPatientName(origin.getPatientName());
            dicomSearchResultDto.setPatientAge(origin.getPatientAge());
            dicomSearchResultDto.setModality(origin.getModality());
            dicomSearchResultDto.setT1Axial(origin.isT1Axial());

            // 부모에 딸려있는 자식들에게서 PNG 파일 경로만 뽑아오기
            List<String> pngPaths = new ArrayList<>();
            for (var conversion : origin.getConversions()) {
                pngPaths.add(conversion.getConvertedFilePath());
            }
            dicomSearchResultDto.setPngImagePaths(pngPaths);
            resultList.add(dicomSearchResultDto);
        }
        return resultList;

    }


    //findById 같은 JPA 기본 메서드는 결과가 없을 수도 있기 때문에 Optional이라는 특수한 상자에 담겨서 나옵니다.
    // 그래서 .orElse(null)을 붙여 "상자에 데이터가 없으면 그냥 null을 줘"라고 처리한 것입니다.)
    public DicomOriginEntity getDetail(Long id) {
        return dicomOriginRepository.findById(id).orElse(null);
    }
}
