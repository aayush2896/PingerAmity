package pinger.slac.com.pingeramity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Author Shiv
public class DeviceBooting extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            long timeMillis = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date resultdate = new Date(timeMillis);

            Log.w("Running", "Running " + sdf.format(resultdate));
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 30);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            Date resultdate2 = new Date(cal.getTimeInMillis());
            Log.w("alarm reset", "alarm reset next alarm at: "+sdf.format(resultdate2));
        }
    }
}



