package com.example.smart_refrigerator_application;

import static android.content.ContentValues.TAG;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RefrigeratorInformationActivity extends AppCompatActivity {

    //내부 DB연결
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;

    //날짜
    public static String format_year = "yyyy";
    public static String format_Month = "MM";
    public static String format_day = "dd";

    //화면 출력용 텍뷰
    TextView tvTemperatureHumidity, tvDistance, tvFoodExpirationDatelist;
    String Distance, Temperature, Humidity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refrigerator_information);

        myHelper = new myDBHelper(this);

        tvDistance = (TextView)findViewById(R.id.tvDistance);
        tvTemperatureHumidity = (TextView)findViewById(R.id.tvTemperatureHumidity);
        tvFoodExpirationDatelist = (TextView)findViewById(R.id.tvFoodExpirationDatelist);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref_Distance = database.getReference("Distance");
        DatabaseReference ref_Temperature = database.getReference("Temperature");
        DatabaseReference ref_Humidity = database.getReference("Humidity");

        //==============================================================================================================
        //Distance값
        ref_Distance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                Distance = value.toString();
                if (Integer.parseInt(Distance) < 10) {
                    //거리가 일정 수치 측정 후 개폐여부 판단하고 출력하자.
                    tvDistance.setText("닫힘");
                } else {
                    tvDistance.setText("열림");
                }
                //            tvFoodExpirationDatelist.setText(gdate); 이거 좀따가
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        //==============================================================================================================
        //Temperature값 Humidity값
        ref_Temperature.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                Temperature = value.toString() + "°";

                tvTemperatureHumidity.setText(Temperature + " / " + Humidity);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        ref_Humidity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                Humidity = value.toString() + "%";

                tvTemperatureHumidity.setText(Temperature + " / " + Humidity);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        //==============================================================================================================
        //유통기한 임박 식품 텍뷰 출력

        String s = "";

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat(format_year, Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat(format_Month, Locale.getDefault());
        SimpleDateFormat format3 = new SimpleDateFormat(format_day, Locale.getDefault());
        String current = format1.format(currentTime) + "-" + format2.format(currentTime) + "-" + format3.format(currentTime);


        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM foodTBL;",null);
        while(cursor.moveToNext()){
            try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
                Date FirstDate = format.parse(cursor.getString(3));
                Date SecondDate = format.parse(current);

                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                // 연산결과 -950400000. long type 으로 return 된다.
                long calDate = FirstDate.getTime() - SecondDate.getTime();

                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                long calDateDays = calDate / ( 24*60*60*1000);

                calDateDays = Math.abs(calDateDays);

                if(calDateDays <= 7){
                    s = s + cursor.getString(1) + "  :  " + cursor.getString(3) + "\n";
                }
            }
            catch(ParseException e)
            {
                // 예외 처리
            }
        }
        tvFoodExpirationDatelist.setText(s);
        cursor.close();
        sqlDB.close();

        //==========================================================================================
        //뒤로 버튼이랑 리스너
        Button btnReturn = (Button) findViewById(R.id.button3);
        btnReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

    }



}
