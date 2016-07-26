package pinger.slac.com.pingeramity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

//Author Shiv
public class AlarmReceiver extends BroadcastReceiver {

    String[] beaconList = {"www.andi.dz","waib.gouv.bj","www.gov.bw","www.univ-ouaga.bf","www.univ-koudougou.bf","www.assemblee.bi","www.anor.cm"};
    File filepath;
    File root;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Pinging Here
        //Update to sever every 24hrs
        try {
            root = new File(Environment.getExternalStorageDirectory(), "PingER");
            if (!root.exists()) {
                root.mkdirs();
            }
            filepath = new File(root, "data.txt");  // file path to save
        }catch (Exception e) {
            e.printStackTrace();
            //  result.setText(e.getMessage().toString());
        }
        Ping(context);
        ResetAlarm(context);

    }

    void ResetAlarm(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        Date resultdate2 = new Date(cal.getTimeInMillis());
        Log.w("alarm reset", "alarm reset next alarm at: "+sdf.format(resultdate2));
    }

    public void Ping(final Context context){
        new Thread(new Runnable() {
            public void run() {
                Random random = new Random();
                for(int i=0;i<beaconList.length;i++){//
                    //int i = random.nextInt(beaconList.length);
                    Editable host = new SpannableStringBuilder(beaconList[i]);

                    Process p = null;
                    try {
                        String pingCmd = "ping -s 100 -c 10 " + host;//-D doesnt work on android
                        String pingResult = "";

                        Runtime r = Runtime.getRuntime();
                        p = r.exec(pingCmd);
                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(p.getInputStream()));
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            //System.out.println("Tester "+inputLine+" space "+inputLine.toLowerCase().contains("rtt"));

                            //Starting point check when ping issued
                            if((!inputLine.toLowerCase().contains("ping"))){
                            }
                            inputLine = "[" + System.currentTimeMillis() / 1000 + "]" + inputLine;

                            System.out.println(inputLine);

                            //keep adding to block
                            pingResult += inputLine+"\n";
                            final String ping_result=pingResult;

                            //Show progress in UI


                            //One set of pings fully received
                            if(inputLine.contains("rtt"))
                            {
                                //Only once rtt is recived we save to file, regex checks on block of code can be performed here b4 saving to file
                                String finalString=RegexMatches.PassesAllTests(ping_result,context);
                                appendToFile(finalString);
                                pingResult="";
                            }
                        }
                        System.out.print("Error Stream: " + p.getErrorStream());
                        in.close();
                    }//try
                    catch (IOException e) {
                        System.out.println(e);
                        System.out.print("Error Stream: " + p.getErrorStream());
                    }
                }
            }
        }).start();


    }

    public void appendToFile(String inputLine){
        try {
            BufferedWriter bW;
            bW = new BufferedWriter(new FileWriter(filepath,true));
            bW.write(inputLine);
            bW.newLine();
            bW.flush();
            bW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}