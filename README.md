# DICOM MetaData Extraction & Conversion System

DICOM 파일을 업로드하면 메타데이터 추출, PNG 변환, DB 저장 및 검색까지 수행하는 Spring Boot 기반 백엔드 시스템입니다. Python(FastAPI)과 연동하여 이미지 분석 및 변환을 처리합니다.

---

## Project Overview

- ZIP 파일 업로드 기반 DICOM 처리
- Python 서버를 통한 이미지 분석 및 PNG 변환
- 메타데이터 DB 저장
- 조건 기반 검색 API 제공

---

## Tech Stack

- Backend: Spring Boot, JPA (Hibernate)
- Python: FastAPI
- Database: MySQL
- Communication: REST API (JSON)
- File Handling: ZipInputStream 기반 스트리밍 처리

---

## Architecture

Client → Spring Boot → Python(FastAPI) → Database

- 클라이언트가 ZIP 파일 업로드
- Spring에서 압축 해제 및 DICOM 추출
- Python 서버로 분석 요청
- 결과를 받아 DB 저장
- 검색 API로 데이터 조회

---

## Setup

### Spring Boot

```bash
./gradlew bootRun
```

### Python (FastAPI)

```bash
uvicorn main:app --reload --port 8000
```

### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dcm_metadata_extraction
spring.datasource.username=root
spring.datasource.password=test1234

spring.jpa.hibernate.ddl-auto=update

spring.servlet.multipart.max-file-size=10GB
spring.servlet.multipart.max-request-size=10GB
```

---

## Data Flow

1. ZIP 파일 업로드
2. 압축 해제 및 DICOM 파일 탐색
3. 임시 디렉토리 저장
4. Python 서버로 분석 요청
5. PNG 변환 및 메타데이터 추출
6. DB 저장

---

## Package Structure

```text
controller/
service/
repository/
entity/
dto/
specification/
```

---

## Database Design

### DicomOrigin

- patientName
- modality
- patientAge
- isT1Axial
- instanceNumber
- originFilePath

### DicomConversion

- convertedFilePath
- conversionDate
- status
- origin_id (FK)

---

## API

### Upload

```http
POST /dicom/upload
```

---

### Search

```http
GET /dicom/search
```

Query Parameters:

```text
isT1Axial=true
modalities=MR,CT
minAge=20
maxAge=60
```

---

### Detail

```http
GET /dicom/search/{id}
```

---

### Convert

```http
POST /dicom/convert/{id}
```

---

## Python Communication

### Request

```json
{
  "input_dicom_file_path": "path",
  "output_png_file_path": "path"
}
```

### Response

```json
{
  "status": "SUCCESS",
  "is_t1_axial": true,
  "patient_name": "홍길동",
  "modality": "MR",
  "patient_age": 45,
  "instance_number": 1,
  "output_png_file_path": "path"
}
```

---

## Key Points

- ZipInputStream 기반 대용량 파일 처리
- Python 서비스 분리 (이미지 처리 책임 분리)
- DTO 기반 응답으로 순환참조 방지
- Specification 기반 동적 검색 구현
- Fetch Join 최적화로 성능 개선

---

## Summary

DICOM 업로드부터 분석, 변환, 저장, 검색까지 이어지는 의료 영상 처리 백엔드 파이프라인입니다.
