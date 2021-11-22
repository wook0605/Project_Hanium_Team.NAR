package com.example.smart_refrigerator_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RefrigeratorSettingActivity extends AppCompatActivity {

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;

    String Humidity = null;
    String Temperature = null;
    String foodExpirationDate = null;
    String on_off = null;
    String openTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //===========================================================================================================
        //화면 출력
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //===========================================================================================================
        //내부 데이터 베이스 연결
        myHelper = new myDBHelper(this);
        //===========================================================================================================
        //설정 버튼 선언 연결
        Switch swOnOff = (Switch)findViewById(R.id.swOnOff);
        RadioGroup rgOpenTime = (RadioGroup)findViewById(R.id.rgOpenTime);
        RadioButton radioButton1 = (RadioButton)findViewById(R.id.radioButton1);
        RadioButton radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
        RadioButton radioButton5 = (RadioButton)findViewById(R.id.radioButton5);
        RadioButton radioButton10 = (RadioButton)findViewById(R.id.radioButton10);
        SeekBar sbFoodExpirationDate = (SeekBar)findViewById(R.id.sbFoodExpirationDate);
        EditText edtTemperature = (EditText)findViewById(R.id.edtTemperature);
        EditText edtHumidity = (EditText)findViewById(R.id.edtHumidity);
        TextView tvFoodExpirationDate_setting = (TextView)findViewById(R.id.tvFoodExpirationDate_setting);
        //===========================================================================================================
        //외부 버튼 선언 연결
        Button btnReturn = (Button) findViewById(R.id.button14);
        Button btnReset = (Button) findViewById(R.id.btnReset);
        Button delAllFood = (Button) findViewById(R.id.delAllFood);
        //===========================================================================================================
        //버튼 리스너 적용
        btnReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //임시 초기화
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 3, 4);
                sqlDB.close();

                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String text = "";   //리셋 발생으로 다음 실행시 최초실행을 진행한다.
                editor.putString("text", text);
                editor.commit();

                Toast.makeText(getApplicationContext(), "데이터 베이스 초기화 완료\n 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        delAllFood.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //임시 초기화
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 3, 4);
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "냉장고 모든 식품을 삭제하였습니다.\n 앱을 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        //===========================================================================================================
        //파이어 베이스 설정 값 읽기 + 읽은 값 출력 화면
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref_setting = database.getReference("setting");
        DatabaseReference ref_openTime = database.getReference("setting");
        DatabaseReference ref_foodExpirationDate = database.getReference("setting");
        DatabaseReference ref_Temperature = database.getReference("setting");
        DatabaseReference ref_Humidity = database.getReference("setting");

        ref_setting.child("on_off").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                on_off = value.toString();
                if(on_off.equals("on")){
                    swOnOff.setChecked(true);
                }
                else if(on_off.equals("off")){
                    swOnOff.setChecked(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        ref_openTime.child("openTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                openTime = value.toString();
                if(openTime.equals("1")){
                    radioButton1.setChecked(true);
                }
                else if(openTime.equals("3")){
                    radioButton3.setChecked(true);
                }
                else if(openTime.equals("5")){
                    radioButton5.setChecked(true);
                }
                else if(openTime.equals("10")){
                    radioButton10.setChecked(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        ref_foodExpirationDate.child("foodExpirationDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                foodExpirationDate = value.toString();
                sbFoodExpirationDate.setProgress(Integer.parseInt(foodExpirationDate));
                tvFoodExpirationDate_setting.setText(foodExpirationDate + "일");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        ref_Temperature.child("Temperature").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                Temperature = value.toString();
                edtTemperature.setText(Temperature);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        ref_Humidity.child("Humidity").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(Object.class);
                Humidity = value.toString();
                edtHumidity.setText(Humidity);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        //===========================================================================================================
        //설정 값 변경시 작동하는 리스너 등록
        swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("setting");
                if(isChecked){
                    //스위치가 off -> on 으로 변경된 경우 실행
                    myRef.child("on_off").setValue("on");
                }
                else {
                    //스위치가 on -> off 로 변경된 경우 실행
                    myRef.child("on_off").setValue("off");
                }
            }
        });
        rgOpenTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("setting");
                if(checkedId == R.id.radioButton1){
                    myRef.child("openTime").setValue("1");
                }
                else if (checkedId == R.id.radioButton3){
                    myRef.child("openTime").setValue("3");
                }
                else if (checkedId == R.id.radioButton5){
                    myRef.child("openTime").setValue("5");
                }
                else if (checkedId == R.id.radioButton10){
                    myRef.child("openTime").setValue("10");

                }
            }
        });
        sbFoodExpirationDate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // onProgressChange - Seekbar 값 변경될때마다 호출
                tvFoodExpirationDate_setting.setText(String.valueOf(seekBar.getProgress() + "일"));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // onStartTeackingTouch - SeekBar 값 변경위해 첫 눌림에 호출
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // onStopTrackingTouch - SeekBar 값 변경 끝나고 드래그 떼면 호출
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("setting");
                myRef.child("foodExpirationDate").setValue(String.valueOf(seekBar.getProgress()));
            }
        });
        edtTemperature.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                //텍스트가 변경 될때마다 Call back
            }
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 입력이 모두 끝았을때 Call back
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("setting");
                myRef.child("Temperature").setValue(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                //텍스트가 입력하기 전에 Call back
            }
        });
        edtHumidity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                //텍스트가 변경 될때마다 Call back
            }
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 입력이 모두 끝았을때 Call back
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("setting");
                myRef.child("Humidity").setValue(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                //텍스트가 입력하기 전에 Call back
            }
        });

    }

}
