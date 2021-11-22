#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "Adafruit_VL53L0X.h"
#include "DHT.h"

#define DHTPIN 2        // SDA 핀의 설정
#define DHTTYPE DHT22   // DHT22 (AM2302) 센서종류 설정

#define FIREBASE_HOST "smart-refrigerator-application-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "ksNx2ieYGtY2kCoygFuDKgjCKTuksLuFCilQpCCy"

Adafruit_VL53L0X TOF = Adafruit_VL53L0X();
DHT dht(DHTPIN, DHTTYPE);

const char* ssid = "JSEA";
const char* password = "rodrodrod";


WiFiClient client;
 
void setup() {
  Serial.begin(115200);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
 
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println(".");
  }
  Serial.println("");
  Serial.print("Connecting to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Serial.println("FIREBASE started");
  dht.begin();
 
  // 일반적으로는 I2C 주소와 디버그 모드 설정값을 넘겨줘서 시작.
  
  if (!TOF.begin()) { // VL53L0X 기본 I2C 주소:0x29, 디버그 모드:false로 센서 준비.
    Serial.println(F("Failed to boot VL53L0X"));
    while(1);
  }
}
 
void loop() {
  String input_Value = "";
  client = server.available();
  VL53L0X_RangingMeasurementData_t measure; // 측정값을 담을 구조체 변수
  if(!client) return;
 
  Serial.println("새로운 클라이언트");
  client.setTimeout(5000);
 
  String request = client.readStringUntil('\r');
  Serial.println("request: ");
  Serial.println(request);
 
  while(client.available()) {
    client.read();
  }

 TOF.rangingTest(&measure, false); // true를 주면 디버그용 데이터를 받아옴
 
  // 이번 측정의 상태값. 장치 의존적인 값. 일반적으로 4면 에러. 0이면 측정값이 정상임을 나타냄.
  if(measure.RangeStatus != 4) {  
    Serial.print("Distance (mm): "); Serial.println(measure.RangeMilliMeter);
  } 
  else {
    Serial.println(" out of range ");
  }
  if(Serial.available() > 0) //Uno 보드에서 보낸 데이터가 있을 경우
  {
    Serial.println("Read_Serial_Data");
    input_Value = Serial.readStringUntil('\n');
  }
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  Serial.print("Humidity : ");
  Serial.print(h);
  Serial.print(" Temperature : ");
  Serial.print(t);
  Serial.println(" ºC");
  //Firebase Realtime Database에 센서 측정값 올리기
  Firebase.setint("Distance:", measure.RangeMilliMeter); 
  Firebase.setfloat("Humidity:", h);
  Firebase.setString("Piezo_Sensor:", input_Value);
  Firebase.setfloat("Temperature:", t);

  delay(3000); //Uno 보드와의 Serial 통신 값이 밀려서 오지 않도록 딜레이를 줌

}
