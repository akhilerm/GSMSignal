package com.slateandpencil.gsmsignal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Akhil on 29-06-2016.
 */
public class DB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SignalDB";
    public static final String SIGNAL_TABLE_NAME = "signal";
    public static final String SIGNAL_ID = "id";
    public static final String TIMESTAMP = "time";
    public static final String SIGNAL_RSSI = "rssi";
    public static final String SIGNAL_BER = "ber";
    public static final String SIGNAL_CID = "cid";
    public static final String TEMP = "temp";
    //public static final String MOT = "motion";

    public DB(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE signal (id integer primary key,time text, rssi text, ber text, cid text, temp text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DELETE * FROM signal");
    }

    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SIGNAL_TABLE_NAME,null,null);
    }

    public int last_row() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM signal WHERE id = (SELECT MAX(id) FROM signal)",null);
        cursor.moveToFirst();
        String time = cursor.getString(1);
        String[] part1 = time.split(":");
        String[] part2 = part1[2].split(" ");
        return Integer.parseInt(part2[0]);
    }

    public void insert(String time,String rssi,String ber,String cid,String temp) {
        Log.e("Checkpoint","INserting into DB");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP,time);
        values.put(SIGNAL_RSSI,rssi);
        values.put(SIGNAL_BER,ber);
        values.put(SIGNAL_CID,cid);
        values.put(TEMP,temp);
        //values.put(MOT,mot);
        db.insert(SIGNAL_TABLE_NAME,null,values);
    }

}
