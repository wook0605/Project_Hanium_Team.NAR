package com.example.smart_refrigerator_application;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;

    public static String format_year = "yyyy";
    public static String format_Month = "MM";
    public static String format_day = "dd";

    EditText edtFoodName, edtFoodCount, edtFoodPosition, edtFoodNote;
    DatePicker fWarehousing, fExpirationDate;    //입고일, 유통기한 데이터 피커용
    String text_fWarehousing = null, text_fExpirationDate = null;   //입고일, 유통기한 데이터 피커 스트링 저장용

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        myHelper = new myDBHelper(this);

        //=======================================================================
        //스피너에 들어갈 문자열 선언
        ArrayList arrayList = new ArrayList();
        //=======================================================================
        //스피너 선언
        final Spinner positionSpinner = (Spinner)findViewById(R.id.spinner_foodPosition);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //=======================================================================
        //파이어 베이스에서 현재 압력 센서값 받아오기
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref_Piezo_Sensor = database.getReference("Piezo_Sensor");
        ref_Piezo_Sensor.addListenerForSingleValueEvent(new ValueEventListener() {
            String Piezo_Sensor;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean check = true;
                Object value = dataSnapshot.getValue(Object.class);
                Piezo_Sensor = value.toString();
                for( int i = 0; i < Piezo_Sensor.length(); i++){
                    if(Piezo_Sensor.charAt(i) == 'H'){
                        arrayList.add(i + "번 위치");
                        check = false;
                    }
                    else if(Piezo_Sensor.charAt(i) == 'L'){
                    }
                }
                if(check){
                    arrayList.add("기타 (현재 센서에 반응이 없습니다.)");
                }
                positionSpinner.setAdapter(adapter);    //스피너에 들어갈 문자열이 나중에 읽어질 수 있어서 여기에 넣는다.
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });


        //========================================================================
        Button btnReturn = (Button) findViewById(R.id.btnAddFoodReturn);
        Button btnSaveFood = (Button) findViewById(R.id.btnSaveFood);

        fWarehousing = (DatePicker)findViewById(R.id.dpFoodWarehousing);
        fExpirationDate = (DatePicker)findViewById(R.id.dpFoodExpirationDate);

        //미설정시 현재 날짜 등록
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat(format_year, Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat(format_Month, Locale.getDefault());
        SimpleDateFormat format3 = new SimpleDateFormat(format_day, Locale.getDefault());
        String current = format1.format(currentTime) + "-" + format2.format(currentTime) + "-" + format3.format(currentTime);
        text_fWarehousing = current;
        text_fExpirationDate = current;

        //데이트 피커 오늘 날짜로 초기화 하고 값이 바뀔때마다 값을 변경하고 String으로 저장하고 있는다.
        fWarehousing.init(fWarehousing.getYear(), fWarehousing.getMonth(), fWarehousing.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        text_fWarehousing = String.format("%d-%d-%d", year, monthOfYear, dayOfMonth).toString();
                    }
                });
        fExpirationDate.init(fExpirationDate.getYear(), fExpirationDate.getMonth(), fExpirationDate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        text_fExpirationDate = String.format("%d-%d-%d",year,monthOfYear,dayOfMonth);
                    }
                });
        //========================================================================

        edtFoodName = (EditText)findViewById(R.id.edtFoodName);
        edtFoodCount = (EditText)findViewById(R.id.edtFoodCount);
        //edtFoodPosition = (EditText)findViewById(R.id.edtFoodPosition);
        edtFoodNote = (EditText)findViewById(R.id.edtFoodNote);

        //========================================================================
        // 뒤로 버튼
        btnReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        //저장 버튼 누른 경우 작업

        btnSaveFood.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                //겹치는 이름이 있는지 확인한다.

                String fname = edtFoodName.getText().toString();
                boolean check = true;
                Cursor cursor;
                sqlDB = myHelper.getReadableDatabase();
                cursor = sqlDB.rawQuery("SELECT * FROM foodTBL;", null);
                while (cursor.moveToNext()) {
                    if (fname.equals(cursor.getString(1).toString())) {
                        check = false;
                        break;
                    }
                }
                cursor.close();
                sqlDB.close();

                //============================================================
                //여기서 저장한다. 필요한 모든 값이 유효한지 판단하고 저장한다. 만약 빈값이 있으면 토스트 메시지
                if(check && !edtFoodName.getText().toString().equals("") && text_fWarehousing != null && text_fExpirationDate != null && Integer.parseInt(edtFoodCount.getText().toString()) > 0 && !positionSpinner.getSelectedItem().toString().equals("")) {
                    sqlDB = myHelper.getWritableDatabase();

                    //sqlDB.execSQL("INSERT INTO foodTBL VALUES ( '" + edtFoodName.getText().toString() + "' , " + text_fWarehousing + " , " + text_fExpirationDate + " , " + Integer.parseInt( edtFoodCount.getText().toString()) + " , " + edtFoodPosition.getText().toString() + " , " + edtFoodnote.getText().toString() + ");");
                    ContentValues values = new ContentValues();
                    values.put("fName",edtFoodName.getText().toString());
                    values.put("fWarehousing",text_fWarehousing);
                    values.put("expirationDate",text_fExpirationDate);
                    values.put("fCount", Integer.parseInt(edtFoodCount.getText().toString()));
                    values.put("fPosition",positionSpinner.getSelectedItem().toString());
                    values.put("note",edtFoodNote.getText().toString());
                    sqlDB.insert("foodTBL",null,values);

                    Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                    outIntent.putExtra("name", edtFoodName.getText().toString());
                    setResult(RESULT_OK,outIntent);
                    sqlDB.close();
                    finish();
                }
                else if (!check){
                    Toast.makeText(getApplicationContext(), "동일한 식품이 이미 냉장고에 존재합니다.", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(edtFoodCount.getText().toString()) < 1){
                    Toast.makeText(getApplicationContext(), "수량을 1이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "메모를 제외한 항목을 모두 작성해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
