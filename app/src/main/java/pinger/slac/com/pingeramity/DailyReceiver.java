package pinger.slac.com.pingeramity;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Author: Shiv
public class DailyReceiver extends BroadcastReceiver {

    File filepath;
    File root;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            root = new File(Environment.getExternalStorageDirectory(), "PingER");
            if (!root.exists()) {
                root.mkdirs();
            }
            filepath = new File(root, "data.txt");  // file path to save
        }catch (Exception e) {
            e.printStackTrace();
        }

        ProxyConnect();
        ResetAlarm(context);
    }

    public void ProxyConnect(){
        Socket socket = null;
        String host = "";//get the correct path
        try {
            Calendar now = Calendar.getInstance();
            int month=(now.get(Calendar.MONTH) + 1);
            int year=now.get(Calendar.YEAR);

            //install tomcat on dabba
            //run server.java
            //send month/year and data.txt to server.java
            //use server.java to get latest file accessing the file system name and send it
            //check on the server if something with same month/year exists
            // if it exists append to same/overwrite file or else create a new file

            socket = new Socket(host, 4444);
            long length = filepath.length();
            byte[] bytes = new byte[32 * 1024];

            InputStream in = new FileInputStream(filepath);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void ResetAlarm(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        Date resultdate2 = new Date(cal.getTimeInMillis());
        Toast.makeText(context,"Working on it",Toast.LENGTH_LONG).show();
        Log.w("alarm reset", "alarm reset next alarm at: "+sdf.format(resultdate2));
    }
}
