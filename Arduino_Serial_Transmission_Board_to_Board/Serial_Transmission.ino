//압전센서 및 압전센서 값 직렬통신

int Value[6];
char msg[7];
int size_ = sizeof(msg)/sizeof(char);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
}

void loop() {

  long F_Value = 1000000;
  Value[0] = analogRead(0);
  Value[1] = analogRead(1);
  Value[2] = analogRead(2);
  Value[3] = analogRead(3);
  Value[4] = analogRead(4);
  Value[5] = analogRead(5);

  for(int i = 0; i<6; i++)
  {
    if(Value[i] /1023 < 1)
    {
      msg[i] = 'L';
    }
    else
    {
      msg[i] = 'H';
    }
  }
  Serial.write(msg, size_);
  while(Serial.available())
  {
    Serial.read();
  }
  delay(10000);
}
