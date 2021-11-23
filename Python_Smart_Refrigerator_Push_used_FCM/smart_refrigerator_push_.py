import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging
from firebase_admin import db
import datetime 

cred = credentials.Certificate("C:\\Firebase_SDK\\smart-refrigerator-application-firebase-adminsdk-dj56y-6a6785c7d5.json")
firebase_admin.initialize_app(cred, {'databaseURL' : 'https://smart-refrigerator-application-default-rtdb.firebaseio.com/'})

def Send_Message(head, text):
    #메시지를 받는 쪽의 토큰
    registration_token = "eWMs1RDLSv2pG03-WVshBq:APA91bH6T2qgeA-q_UpMblIPIKd9Hn6pdRuc-XmncfV01mVbTjavgKzvciDkqJy3XufsIzgOa2oE-l0Pzj7ZR-5nDdThnJ3IQmgekpyafveVukzvGTvD0u4TTeLDgDIpHdQ9A2Ht272O"
    #메시지 보내기
    message = messaging.Message(
        notification=messaging.Notification(
            title = head,
            body = text,
        ),
        token = registration_token
    )
    try:
        response = messaging.send(message)
        print('메시지가 성공적으로 전송되었습니다.', response)
    except Exception as e:
        print('예외가 발생했습니다.', e)

time_limit = [datetime.timedelta(hours = 1) - datetime.timedelta(hours = 0), 0] #메시지 전송 인터벌 1시간
interval = [0, 0] #현재시간, 경과시간
while True:        
    Data_Base_ = [db.reference("Distance").get(), #거리 값
                db.reference("Humidity").get(),  #습도
                db.reference("Temperature").get()] #온도
    Setting = [db.reference("setting/on_off").get(), #알림 on, off
                int(db.reference("setting/Humidity").get()), #습도 경고임계값
               int(db.reference("setting/Temperature").get()), #온도 경고임계값
               int(db.reference("setting/foodExpirationDate").get()), #유통기한 경고임계값
               int(db.reference("setting/openTime").get())] #문열림 경고 임계시간 (분)
    time_limit[1] = datetime.timedelta(minutes= Setting[4]) - datetime.timedelta(minutes=0)
    if interval[0] == 0:
        interval[0] = datetime.datetime.now()
    #Data_Base_ 리스트의 원소 자료형 변환============================
    #Setting 리스트의 자료형 변환============================
    #Setting = [bool, int, int, int, int]
    if Setting[0] == "on":
        Setting[0] = True
    else :
        Setting[0] = False
    if Setting[0] : #알람 ON일 때
        if interval[1] == 0 or check >= time_limit[0]: #이전 메시지 전송 기록이 없거나, 전송한지 1시간이 넘었을 때
           if Data_Base_[1] > Setting[1] :
                Send_Message("습도 이상경고!", "냉장고 내부 습도가 너무 높습니다!")
           if Data_Base_[2] > Setting[2] : 
                Send_Message("온도 이상경고!", "냉장고 내부 온도가 너무 높습니다!")    
        if interval[1] != 0:
            interval[0] = datetime.datetime.now()        
        interval[1] = datetime.datetime.now()
        check = interval[1] - interval[0]
        
        if Data_Base_[0] > 10  and check >= time_limit[1]: # 문열림 경고
            Send_Message("문 열림 경고!", "냉장고 문이 열려있습니다!")