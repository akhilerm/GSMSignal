package com.slateandpencil.gsmsignal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

    public MyService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
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
        Toast.makeText(MyService.this, "Data Fetch Terminated", Toast.LENGTH_SHORT).show();
    }

    public class myPhoneStateListener extends PhoneStateListener {
        public int signalStrengthValue;
        public int berValue;
        Calendar calendar = Calendar.getInstance();
        DB db = new DB(MyService.this);

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.e("Checkpoint","INside on signal changed");
            super.onSignalStrengthsChanged(signalStrength);
            GsmCellLocation gsmCellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
            int seconds = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(calendar.HOUR_OF_DAY);
            int day = calendar.get(calendar.DAY_OF_MONTH);
            int month = calendar.get(calendar.MONTH);
            int year = calendar.get(calendar.YEAR);
            signalStrengthValue = signalStrength.getGsmSignalStrength();
            berValue = signalStrength.getGsmBitErrorRate();
            db.insert(hour+":"+minute+":"+seconds+" "+day+"/"+month+"/"+year,Integer.toString(signalStrengthValue),Integer.toString(berValue),Integer.toString(gsmCellLocation.getCid()));
        }
    }
}
