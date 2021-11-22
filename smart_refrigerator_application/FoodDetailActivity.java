package com.example.smart_refrigerator_application;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailActivity extends AppCompatActivity {

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    int getID;      //현재 출력하고 있는 식품 데이터 베이스 아이디값

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myHelper = new myDBHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooddetail);

        Intent inIntent = getIntent();
        final String i_fName = inIntent.getExtras().getString("fname").toString();
        final int btnId = inIntent.getExtras().getInt("btnId");

        //========================================================================================================================
        //텍뷰 선언 연결
        TextView tvFoodname = (TextView) findViewById(R.id.tvFoodname);
        TextView tvFoodWarehousing = (TextView) findViewById(R.id.tvFoodWarehousing);
        TextView tvFoodExpirationDate = (TextView) findViewById(R.id.tvFoodExpirationDate);
        TextView tvFoodCount = (TextView) findViewById(R.id.tvFoodCount);
        TextView tvFoodPosition = (TextView) findViewById(R.id.tvFoodPosition);
        TextView tvFoodNote = (TextView) findViewById(R.id.tvFoodNote);
        //========================================================================================================================
        String s = "";
        Cursor cursor;
        sqlDB = myHelper.getReadableDatabase();
        cursor = sqlDB.rawQuery("SELECT * FROM foodTBL WHERE fName ='" + i_fName + "';", null);
        cursor.moveToNext();                //이거 필수임 ㅡㅡ
        getID = Integer.parseInt(cursor.getString(0));
        tvFoodname.setText(cursor.getString(1).toString());
        tvFoodWarehousing.setText(cursor.getString(2).toString());
        tvFoodExpirationDate.setText(cursor.getString(3).toString());
        tvFoodCount.setText(cursor.getString(4));
        tvFoodPosition.setText(cursor.getString(5));
        tvFoodNote.setText(cursor.getString(6));
        cursor.close();
        sqlDB.close();
        //========================================================================================================================
        //버튼 선언 연결
        Button btnFoodDetailReturn = (Button) findViewById(R.id.btnFoodDetailReturn);
        Button btnFoodDetailUse = (Button) findViewById(R.id.btnFoodDetailUse);
        Button btnFoodDetailModify = (Button) findViewById(R.id.btnFoodDetailModify);
        Button btnFoodDetailDelete = (Button) findViewById(R.id.btnFoodDetailDelete);
        //========================================================================================================================
        //뒤로버튼 리스너
        btnFoodDetailReturn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        //사용 버튼 리스너
        btnFoodDetailUse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //수량 1 감소 만약 수량이 0이면 삭제 루트-> 데이터 베이스 변경 -> 텍뷰 변경
                int i = Integer.parseInt(tvFoodCount.getText().toString()) - 1;
                if(i == 0){
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.delete("foodTBL","_fid=" + getID,null);
                    sqlDB.close();

                    Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                    outIntent.putExtra("btnId", btnId);
                    setResult(2,outIntent);
                    Toast.makeText(getApplicationContext(), "해당 식품을 모두 사용하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    ContentValues values = new ContentValues();
                    values.put("fCount", i);
                    tvFoodCount.setText(String.valueOf(i));
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.update("foodTBL", values, "_fid=" + getID,null);
                    sqlDB.close();
                }
                Toast.makeText(getApplicationContext(), "해당 식품을 사용하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        //수정 버튼 리스너
        btnFoodDetailModify.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //이거 필요 없을거 같다 삭제 ㅇㅇㅇ
               finish();
            }
        });
        //삭제 버튼 리스너
        btnFoodDetailDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.delete("foodTBL","_fid=" + getID,null);
                sqlDB.close();

                Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                outIntent.putExtra("btnId", btnId);
                setResult(2,outIntent);
                finish();
            }
        });
    }
}

