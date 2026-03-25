package MedicalDicom.MetaData_Extraction.service;

import MedicalDicom.MetaData_Extraction.repository.DicomConversionRepository;
import MedicalDicom.MetaData_Extraction.repository.DicomOriginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DicomExtractorService {
    private final DicomOriginRepository dicomOriginRepository;
    private final DicomConversionRepository dicomConversionRepository;


    // 압축 파일을 받아서 처리하는 메인 메서드 여기서 수만 개의 파일 중 하나라도 실패하면 전체 취소되어야 함
    // ZIP 파일도 MultipartFile 파일의 확장자나 종류(JPG, PNG, PDF, ZIP 등)와
    // 상관없이 HTTP 프로토콜을 통해 업로드되는 파일 데이터를 서버에서 다루는 방식
    @Transactional
    public void processZipFile(MultipartFile multipartFile) {
        // 압축파일을 받아서 압축 파일 내부의 파일이 더 이상 나오지 않을 때까지 확인하며 도는 것.
        try (ZipInputStream zipInputStream = new ZipInputStream(multipartFile.getInputStream())) {
            ZipEntry zipEntry; //ZipEntry는 파일 내의 각 개별 파일(엔트리)을 나타냄

            // 각 개별 파일이 null 이 아닌지 확인함. 이렇게 해야 동작함. 알아둘 것.
            while((zipEntry = zipInputStream.getNextEntry())!= null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith("dcm")) {
                    log.info("DICOM 파일 발견: " + zipEntry.getName());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
