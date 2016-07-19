package com.slateandpencil.gsmsignal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class MyService extends Service implements SensorEventListener {

    TelephonyManager telephonyManager;
    myPhoneStateListener psListener;
    private BatInfoReceiver batInfoReceiver;
    SensorManager sensorManager;
    Sensor sensor;
    float x,y;

    public MyService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        batInfoReceiver = new BatInfoReceiver();
        this.registerReceiver(this.batInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Log.e("Checkpoint","INside on create in service");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this, "Data Fetch Initiated", Toast.LENGTH_SHORT).show();
        Log.e("Checkpoint","INside on startcommand in service");
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        psListener = new myPhoneStateListener();
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(psListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(this.batInfoReceiver);
        stopSelf();
        Toast.makeText(MyService.this, "Data Fetch Terminated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy) {
        //Implemented Abstract Method
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
           x = sensorEvent.values[0];
           y = sensorEvent.values[1];
           Log.e("Inside Sensor Changed",x+" , "+y);
    }

    public class myPhoneStateListener extends PhoneStateListener {
        public float signalStrengthValue;
        public int berValue;

        DB db = new DB(MyService.this);


        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.e("Checkpoint","INside on signal changed");
            super.onSignalStrengthsChanged(signalStrength);
            Calendar calendar = Calendar.getInstance();
            //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            GsmCellLocation gsmCellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
            int seconds = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(calendar.HOUR_OF_DAY);
            int day = calendar.get(calendar.DAY_OF_MONTH);
            int month = calendar.get(calendar.MONTH);
            int year = calendar.get(calendar.YEAR);
            //float x = sensor;
            float temp = batInfoReceiver.get_temp();
            signalStrengthValue = signalStrength.getGsmSignalStrength();
            berValue = signalStrength.getGsmBitErrorRate();
            int t = db.last_row();
            if((t+2)%60 == seconds)
                db.insert(hour+":"+minute+":"+seconds+" "+day+"/"+month+"/"+year,Float.toString(signalStrengthValue),Integer.toString(berValue),Integer.toString(gsmCellLocation.getCid()),Float.toString(temp));
            else if ((db.last_row()+2)%60 > seconds) {
                int i=2;
                while((t+i)%60>=seconds) {
                    db.insert(hour+":"+minute+":"+(t+i)+" "+day+"/"+month+"/"+year,Float.toString(signalStrengthValue),Integer.toString(berValue),Integer.toString(gsmCellLocation.getCid()),Float.toString(temp));
                    i+=2;
                }
            }
        }
    }
}
