package com.slateandpencil.gsmsignal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Akhil on 29-06-2016.
 */
public class DB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SignalDB";
    public static final String SIGNAL_TABLE_NAME = "signal";
    public static final String SIGNAL_ID = "id";
    public static final String SIGNAL_RSSI = "rssi";
    public static final String SIGNAL_BER = "ber";
    public static final String SIGNAL_LOCATION = "loc";
    public static final String SIGNAL_CID = "cid";

    public DB(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE signal (id integer primary key, rssi text, ber text, loc text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DELETE * FROM signal");
    }

    public void insert(String rssi,String ber,String cid,String loc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SIGNAL_RSSI,rssi);
        values.put(SIGNAL_BER,ber);
        values.put(SIGNAL_CID,cid);
        values.put(SIGNAL_LOCATION,loc);
        db.insert(SIGNAL_TABLE_NAME,null,values);
    }

}
