package com.example.smart_refrigerator_application;

public class smart_refrigerator_application_default_rtdb {

    private String Distance;
    private String Humidity;
    private String Piezo_Sensor;
    private String Temperature;
    private String number;


    public smart_refrigerator_application_default_rtdb() {
    }


    public String getDistance() {
        return Distance;
    }

    public void setDistance(String Distance) {
        this.Distance = Distance;
    }

    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String Humidity) {
        this.Humidity = Humidity;
    }

    public String getPiezo_Sensor() {
        return Piezo_Sensor;
    }

    public void setPiezo_Sensor(String Piezo_Sensor) {
        this.Piezo_Sensor = Piezo_Sensor;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String Temperature) {
        this.Temperature = Temperature;
    }

    public String getnumber() {
        return number;
    }

    public void setnumber(String number) {
        this.number = number;
    }


    //이거는 그룹을 생성할때 사용하는 부분
    public smart_refrigerator_application_default_rtdb(String Distance, String Humidity, String Piezo_Sensor, String Temperature, String number) {
        this.Distance = Distance;
        this.Humidity = Humidity;
        this.Piezo_Sensor = Piezo_Sensor;
        this.Temperature = Temperature;
        this.number = number;
    }

}
