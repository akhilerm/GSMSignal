package com.slateandpencil.gsmsignal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Akhil on 09-07-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("check point"," triggerinng");
        Toast.makeText(context, " Triggered", Toast.LENGTH_SHORT).show();
    }
}
