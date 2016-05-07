package pinger.slac.com.pingeramity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class MainActivity extends Activity implements View.OnClickListener {

    TextView text;
    String[] beaconList = {"192.168.1.1","www.andi.dz","waib.gouv.bj","www.gov.bw","www.univ-ouaga.bf","www.univ-koudougou.bf","www.assemblee.bi","www.anor.cm"};
    File root;
    File filepath;
    FileWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, beaconList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        text = (TextView) findViewById(R.id.tvDisplay);
        Button button = (Button) findViewById(R.id.bPing);
        button.setOnClickListener(this);


        //Setup of alarm manger and PI
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        //Setting 30mns ping interval
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        //Formatting and setting up first alarm at a 30 mns interval
        long timeMillis =  cal.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(timeMillis);
        Log.w("Setting First Alarm","Setting First Alarm at: "+sdf.format(resultdate));
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();


       //Setting up file
        MakeFile();
      }

    public void MakeFile(){
        try {
            root = new File(Environment.getExternalStorageDirectory(), "PingER");
            if (!root.exists()) {
                root.mkdirs();
            }
            //  String h = DateFormat.format("MM-dd-yyyyy-h-mmssaa", System.currentTimeMillis()).toString();
            // this will create a new name everytime and unique
            filepath = new File(root, "data.txt");  // file path to save

            //  String m = "File generated with name " + h + ".txt";
            Log.w("File Setup","File Setup");
            //   result.setText(m);
        } catch (Exception e) {
            e.printStackTrace();
            //  result.setText(e.getMessage().toString());
        }
    }

    public void appendToFile(){
        try {
            BufferedWriter bW;
            bW = new BufferedWriter(new FileWriter(filepath,true));
            bW.write(text.getText().toString());
            bW.newLine();
            bW.flush();
            bW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPing:
                new Thread(new Runnable() {
                    public void run() {
                        Random random = new Random();
                        for(int i=0;i<beaconList.length;i++){
                        //int i = random.nextInt(beaconList.length);
                        Editable host = new SpannableStringBuilder(beaconList[i]);
                        Log.w("Test", "Test " + i);
                        Process p = null;
                        try {
                            String pingCmd = "ping -s 100 -c 10 " + host;//-D doesnt work on android
                            String pingResult = "";

                            Runtime r = Runtime.getRuntime();
                            p = r.exec(pingCmd);
                            BufferedReader in = new BufferedReader(new
                                    InputStreamReader(p.getInputStream()));
                            String  inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                inputLine="["+System.currentTimeMillis()/1000+"]"+inputLine;
                                System.out.println(inputLine);

                                final String  var=inputLine;
                                text.post(new Runnable() {
                                    public void run() {
                                        text.setText(var + "\n\n");
                                    }
                                });
                                pingResult += inputLine;
                                final String  ping_result=pingResult;

                                //PARSE THE STRING TO THE REQUIRED FORMAT HERE and put it in pingResult so it can automatically be saved to the file...
                                text.post(new Runnable() {
                                    public void run() {
                                        text.setText(ping_result);
                                    }
                                  });
                                appendToFile();
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

                    break;
                }
        }


}


