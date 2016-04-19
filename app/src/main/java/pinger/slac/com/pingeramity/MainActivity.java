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

    //TestCommit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, beaconList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        /* Retrieve a PendingIntent that will perform a broadcast */
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent  pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        long timeMillis =  cal.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(timeMillis);
        Log.w("Setting First Alarm","Setting First Alarm at: "+sdf.format(resultdate));
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();


        text = (TextView) findViewById(R.id.tvDisplay);
        Button button = (Button) findViewById(R.id.bPing);
        button.setOnClickListener(this);
        MakeFile();

      }

    public void MakeFile(){
        try {
            root = new File(Environment.getExternalStorageDirectory(), "PingER");
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
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
            //  result.setText(e.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPing:
                Random random = new Random();
                //   int i = random.nextInt(beaconList.length);
                for (int i = 0; i < beaconList.length; i++) {
                    Editable host = new SpannableStringBuilder(beaconList[i]);
                    Log.w("Test", "Test " + i);
                    try {
                        String pingCmd = "ping -c 5 " + host;
                        String pingResult = "";
                        Runtime r = Runtime.getRuntime();
                        Process p = r.exec(pingCmd);
                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(p.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            System.out.println(inputLine);
                            text.setText("Ping For: " + beaconList[i] + "  " + i);

                            text.setText(inputLine + "\n\n");
                            pingResult += inputLine;
                            text.setText(pingResult);
                            appendToFile();
                        }
                        in.close();
                    }//try
                    catch (IOException e) {
                        System.out.println(e);
                    }
                    break;

                }
        }
    }

}
