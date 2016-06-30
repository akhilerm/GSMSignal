package com.slateandpencil.gsmsignal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton control = (FloatingActionButton) findViewById(R.id.control);
        final SharedPreferences sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);

        isRunning = sharedPreferences.getBoolean("Status",false);
        Log.e("First Run",isRunning.toString());
        if(!isRunning) {
            control.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        else {
            control.setImageResource(R.drawable.ic_stop_white_24dp);
        }
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isRunning) {
                    isRunning=false;
                    control.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    stopDataFetch();
                    Snackbar.make(v,"Data Fetch Terminated",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
                else {
                    isRunning=true;
                    control.setImageResource(R.drawable.ic_stop_white_24dp);
                    startDataFetch();
                    Snackbar.make(v,"Data Fetch Initiated",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
                editor.putBoolean("Status",isRunning);
                editor.commit();
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
    public void exportAsCSV(){

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
