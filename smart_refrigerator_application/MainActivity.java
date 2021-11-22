package com.example.smart_refrigerator_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar.LayoutParams;      //이거 추가
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;

    private boolean reset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 최초 실행 확인 -> 데이터 베이스 읽음 -> 버튼 생성 -> 메인 화면 출력

        //================================================================================================================================================================
        //데이터 베이스 헬퍼 연결
        myHelper = new myDBHelper(this);
        //================================================================================================================================================================
        //최초실행 확인 네임파일 찾기
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        String text = sf.getString("text", "");
        if (text.equals("")) {      //빈 문자열 -> 최초실행화면
            //여기에 최초실행화면 넣기
            Intent intent = new Intent(getApplicationContext(), FirstExecutionActivity.class);
            startActivity(intent);
            //최초 실행시 기본 설정 값 파이어 베이스에 전달
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("setting");

            myRef.child("on_off").setValue("on");
            myRef.child("openTime").setValue("3");
            myRef.child("foodExpirationDate").setValue("7");
            myRef.child("Temperature").setValue("8");      //기본 8도
            myRef.child("Humidity").setValue("35");         //기본치 35%

        }
        //================================================================================================================================================================
        //메인 화면 출력
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //================================================================================================================================================================
        //메인화면 중앙 식품 버튼 생성
        LinearLayout msvLinearLayout = (LinearLayout) findViewById(R.id.msvLinearLayout);

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM foodTBL;", null);
        while (cursor.moveToNext()) {
            creativeButton(cursor.getString(1));
        }
        cursor.close();
        sqlDB.close();
        //================================================================================================================================================================
        //하단 버튼 선언 및 정리
        Button btnRefrigeratorInformation, btnAddFood, btnRefrigeratorSetting;
        //냉장고 정보 버튼
        btnRefrigeratorInformation = (Button) findViewById(R.id.refrigerator_Information_button);
        //냉장고 식재료 추가 버튼
        btnAddFood = (Button) findViewById(R.id.add_ingredient_button);
        //냉장고 설정 버튼
        btnRefrigeratorSetting = (Button) findViewById(R.id.refrigerator_setting_button);
        //================================================================================================================================================================
        //냉장고 정보 버튼 클릭 리스너
        btnRefrigeratorInformation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), RefrigeratorInformationActivity.class);
                startActivity(intent);

            }
        });
        //================================================================================================================================================================
        //식품 추가 버튼 클릭 리스너
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AddFoodActivity.class);
                startActivityForResult(intent, 0);

            }
        });
        //================================================================================================================================================================
        //어플 냉장고 설정 버튼 클릭 리스너
        btnRefrigeratorSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RefrigeratorSettingActivity.class);
                startActivity(intent);

            }
        });
    }

    //================================================================================================================================================================
    //종료시 실행하는 부분
    @Override
    protected void onStop() {
        //종료할 때 최초실행을 이미 완료했음을 저장한다.
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String text = "The first execution was completed."; //이미 최초실행을 완료했음을 저장
        editor.putString("text", text);
        editor.commit();
    }

    //================================================================================================================================================================
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {  //식품추가의 저장버튼 사용
            String s = data.getExtras().getString("name").toString();
            creativeButton(s);
        } else if (resultCode == 2) {   //2는 삭제에 사용
            int btnId = data.getExtras().getInt("btnId");
            Button b = (Button) findViewById(btnId);
            b.setEnabled(false);        //버튼 비활성화
            b.setVisibility(View.GONE); //아에 없는 것 처럼 취급한다.
            Toast.makeText(getApplicationContext(), "해당 식품을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
        } else if (resultCode == 3) {   //3은 초기화에 사용
            reset = true;
        }
    }

    //================================================================================================================================================================
    //메인 화면 식품 버튼 생성 메소드
    private static int i = 0;

    public void creativeButton(String s) {
        i++;
        final LinearLayout lm = (LinearLayout) findViewById(R.id.msvLinearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 300);
        params.gravity = Gravity.CENTER;

        final Button btnFood = new Button(this);
        btnFood.setId(i);
        btnFood.setText(s);
        btnFood.setTextSize(20);
        btnFood.setLayoutParams(params);

        btnFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                intent.putExtra("fname", s);
                intent.putExtra("btnId", i);
                startActivityForResult(intent, 0);
            }
        });
        lm.addView(btnFood);
    }

}