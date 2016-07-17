package com.slateandpencil.gsmsignal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
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

public class MyService extends Service {

    TelephonyManager telephonyManager;
    myPhoneStateListener psListener;
    private BatInfoReceiver batInfoReceiver;
    //SensorManager sensorManager;
    //Sensor sensor;

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
            int milliseconds = calendar.get(Calendar.MILLISECOND);
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
            db.insert(hour+":"+minute+":"+seconds+":"+milliseconds+" "+day+"/"+month+"/"+year,Float.toString(signalStrengthValue),Integer.toString(berValue),Integer.toString(gsmCellLocation.getCid()),Float.toString(temp));
           /* Log.e("Battery info","Current " + BatteryManager.EXTRA_TEMPERATURE + " = " +
                    temp +  Character.toString ((char) 176) + " C");*/
        }
    }
}
