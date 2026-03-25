import fastapi as FastAPI
import pydicom

app = FastAPI()

@app.get("/")
def heath_check():
    return {
        "message": "FastAPI 메타데이터 분석 서버가 정상 작동 중입니다!"
    }

@app.get("/analyze/dummy") #말 그대로 더미
def analze_dummy():
    return {
        "status": "success",
        "is_t1_axial": True,
        "patient_name":"김스프링",
        "modality": "MR"
    }

# 1. 스프링이 보낸 DICOM 파일을 받는다.
# 2. ds = pydicom.dcmread(파일) 로 뜯어본다.
# 3. 메타데이터를 분석해서 T1 Axial인지 판별한다.

# [현재 모킹(가짜) 로직]
# 복잡한 1~3번 과정은 일단 스킵!
# 통신이 잘 되는지 확인하기 위해, 무조건 분석에 성공했다고 치고 아래 JSON을 스프링에게 던져준다!

