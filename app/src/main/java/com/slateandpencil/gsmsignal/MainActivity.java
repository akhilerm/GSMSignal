package com.slateandpencil.gsmsignal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    Boolean isRunning;
    TextView textView;
    DB db;
    final int MY_PERMISSIONS_REQUEST_WRITE=20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DB(this);


        final FloatingActionButton control = (FloatingActionButton) findViewById(R.id.control);
        final SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final FloatingActionButton export = (FloatingActionButton) findViewById(R.id.export);
        textView = (TextView)findViewById(R.id.text);

        isRunning = sharedPreferences.getBoolean("Status", false);

        if (!isRunning) {
            control.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            control.setImageResource(R.drawable.ic_stop_white_24dp);
        }


        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isRunning) {
                    isRunning = false;
                    control.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    stopDataFetch();
                    Snackbar.make(v, "Data Fetch Terminated", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    isRunning = true;
                    control.setImageResource(R.drawable.ic_stop_white_24dp);
                    startDataFetch();
                    Snackbar.make(v, "Data Fetch Initiated", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                editor.putBoolean("Status", isRunning);
                editor.commit();
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE);
                }
                else{
                    exportAsCSV();
                    Snackbar.make(v,"Data exported",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });

    }

    //Method to start the service
    public void startDataFetch() {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    // Method to stop the service
    public void stopDataFetch() {
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    //Method to export Data as CSV
    public void exportAsCSV() {
        Log.e("Checkpoint","exporting");
        SQLiteDatabase sqLiteDatabase = (new DB(this).getReadableDatabase());
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("select * from signal",null);
            Log.e("Checkpoint","exporting 1");
            int rowCount;
            int columnCount;
            File file = Environment.getExternalStorageDirectory();

            String filename = "SigData.csv";
            File savefile = new File(file,filename);
            FileWriter fileWriter = new FileWriter(savefile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            rowCount = cursor.getCount();
            columnCount = cursor.getColumnCount();
            if(rowCount > 0) {
                cursor.moveToFirst();
                for (int i=0;i < columnCount;i++) {
                    if(i!=columnCount-1) {
                        bufferedWriter.write(cursor.getColumnName(i)+",");
                    }
                    else {
                        bufferedWriter.write(cursor.getColumnName(i));
                    }
                }
                bufferedWriter.newLine();
                for (int i=0;i < rowCount;i++) {
                    cursor.moveToPosition(i);
                    for (int j=0;j < columnCount;j++) {
                        if(j!=columnCount-1) {
                            bufferedWriter.write(cursor.getString(j)+",");
                        }
                        else {
                            bufferedWriter.write(cursor.getString(j));
                        }
                    }
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        }
        catch (Exception e){
            Log.e("Checkpoint",e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            db.reset();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    exportAsCSV();
                } else {
                    //Permission Denied
                    Toast.makeText(MainActivity.this, "App does not have enough permissions", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}