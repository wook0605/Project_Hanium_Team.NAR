package com.example.smart_refrigerator_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDBHelper extends SQLiteOpenHelper {
    public myDBHelper(Context context){
        super(context, "foodDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS foodTBL ("
                + "_fid INTEGER PRIMARY KEY autoincrement,"
                + "fName text,"             //1
                + "fWarehousing text,"      //2
                + "expirationDate text,"    //3
                + "fCount INTEGER,"         //4
                + "fPosition text,"         //5
                + "note text);";            //6

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS foodTBL");
        onCreate(db);
    }
}
