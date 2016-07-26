package pinger.slac.com.pingeramity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pinger.slac.exceptions.BytesNotFoundException;
import pinger.slac.exceptions.ICMPNotFoundException;
import pinger.slac.exceptions.IPAddressNotFoundException;
import pinger.slac.exceptions.ReceivedNotFoundException;
import pinger.slac.exceptions.TTLNotFoundException;
import pinger.slac.exceptions.TimeNotFoundException;
import pinger.slac.exceptions.TimeStampNotFoundException;
import pinger.slac.exceptions.TransmittedNotFoundException;
import pinger.slac.exceptions.URLNotFoundException;

public class RegexMatches
{

    public static String PassesAllTests(String pingData, Context contenxt){
        String finalString="";

        /* Split ping of every line of data
        String[] everyLine=pingData.split("\n");
        for (int i=0;i<everyLine.length;i++) {
        //    Log.w("Line " + i,everyLine[i]);
        }
         */

        //Try all the checks here and extract the necessary data using the regex below pingData is one block of ping data i.e always complete either you get all the data or you get nothing ensuring consistency in the result
        try {
            String hostName=android.os.Build.MODEL;
            String hostIP=getHostIp(contenxt);
            String groupTimestamp = parseGroupTimestamp(pingData);
            String groupIP = parseGroupIP(pingData);
            String groupURL = parseGroupURL(pingData);
            String[] groupBytes = parseGroupBytes(pingData);
            int packetsSent= getNumberOfPacketsSent(pingData);
            int packetsReceived= getNumberOfPacketsReceived(pingData);
            String icmpCount=countICMP(pingData);
            String icmpTimeStamps=timeOFEachIcmp(pingData);
            String[] statistics=parsePingStatisticsMinAvgMaxMdev(pingData);
            String min = statistics[0];
            String avg = statistics[1];
            String max = statistics[2];
          //  getAllStatisticData(pingData);


            finalString+=hostName+" "+hostIP+" "+groupURL+" "+groupIP+" "+groupBytes[0]+" "+groupTimestamp+"  "+packetsSent+" "+packetsReceived+" "+min+" "+avg+" "+max+" "+icmpCount+" "+icmpTimeStamps;
        }
        catch (Exception exceptions){
            exceptions.printStackTrace();
        }
        Log.w("Final String ","final string "+finalString);
        return finalString;

    }

    public static String getHostIp(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiinfo = wm.getConnectionInfo();
        byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
        Byte[] byteObjects = new Byte[myIPAddress.length];
        int i = 0;
        // Associating Byte array values with bytes. (byte[] to Byte[])
        for (byte b : myIPAddress)
            byteObjects[i++] = b;  // Autoboxing.
        // you must reverse the byte array before conversion. Use Apache's commons library

        List<Byte> byteList = Arrays.asList(byteObjects);
        Collections.reverse(byteList);
        try {
            InetAddress myInetIP = InetAddress.getByAddress(myIPAddress);
            String myIP = myInetIP.getHostAddress();
            return myIP;
        }
        catch (Exception e){
            e.printStackTrace();
            return "No Host";
        }
    }

    public static String parse1(String input){
        Pattern p =Pattern.compile("\\[([0-9]{10})\\]PING");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            return "error";
    }

    //  Works!
    public static String parseGroupTimestamp(String input) throws TimeStampNotFoundException {
        // Capture the [unix-timestamp] before 'PING'
        Pattern p = Pattern.compile("\\[([0-9]{10})\\]PING");
        Matcher m = p.matcher(input);
        int x;
        if (m.find())
            return m.group(1);
        else
            throw new TimeStampNotFoundException();
    }

    public static int getNumberOfPacketsSent(String input) {
        Pattern p = Pattern.compile("\\d+ packets");
        Matcher m = p.matcher(input);
        if (m.find())
            return Integer.parseInt(m.group(0).replaceAll("[\\D]", ""));
        else
            return 0;

    }

    public static int getNumberOfPacketsReceived(String input) {
        Pattern p = Pattern.compile("\\d+ received");
        Matcher m = p.matcher(input);
        if (m.find())
            return Integer.parseInt(m.group(0).replaceAll("[\\D]", ""));
        else
            return 0;

    }

    public static String countICMP(String input) {
        Pattern p = Pattern.compile("icmp_seq=");
        Matcher m = p.matcher(input);
        int i=0;
        String icmp="";
        while (m.find()){
            i++;
            icmp+=i+" ";
        }
        return icmp;
    }

    public static String timeOFEachIcmp(String input) {
        Pattern p = Pattern.compile("time=\\d+");
        Matcher m = p.matcher(input);
        int i=0;
        String timeForEachIcmp="";
        while (m.find()){
            timeForEachIcmp+=Integer.parseInt(m.group(0).replaceAll("[\\D]", ""))+" ";
        }
        return timeForEachIcmp;
    }

    public static void getAllStatisticData(String input){
        Pattern p = Pattern.compile("-?[0-9]+(?:,[0-9]+)?");
        Matcher m = p.matcher(input);

        ArrayList<Double> statisticData=new ArrayList<>();
        int i=0;
        while (m.find()) {
            statisticData.add(Double.parseDouble(m.group(0)));
            System.out.println(m.group()+"  "+statisticData.get(i));
            i++;
        }
    }

    //  Works!
    public static String parseGroupURL(String input) throws URLNotFoundException {
        // Capture the url after 'PING'
        Pattern p = Pattern.compile("PING\\s+([\\w,\\.]+)\\s+\\(");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new URLNotFoundException();
    }

    //  Works!
    public static String parseGroupIP(String input) throws IPAddressNotFoundException {
        //  Capture the (ip-address) after the url
        Pattern p = Pattern.compile("\\(([0-9,\\.]+)\\)");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new IPAddressNotFoundException();
    }

    // Doesn't Work...
    // patterns seem to be correct but only shows the first value
    public static String[] parseGroupBytes(String input) throws BytesNotFoundException {
        //  Capture the bytes after (ip-address) outside the parenthesis
        //  Capture the bytes after (ip-address) inside the parenthesis
        Pattern p1 = Pattern.compile("\\)\\s+(\\d+)\\(");
        Matcher m1 = p1.matcher(input);
        Pattern p2 = Pattern.compile("\\((\\d+)\\)\\s+bytes");
        //     Matcher m2 = p2.matcher(input);
        String[] GroupBytes = new String[2];
        int x = m1.groupCount();
        //    int y = m2.groupCount();
        if(m1.find() ){//        if(m1.find() ){//
            GroupBytes[0] = m1.group(1);
//            GroupBytes[1] = m2.group(1);
            return GroupBytes;

        }
        else
            throw new BytesNotFoundException();
    }

    // Doesn't Work...
    public static String[] parseIndividualTimestamp(String input) throws TimeStampNotFoundException {
        // Capture the [unix-timestamp] before 'x byes from'
        Pattern p = Pattern.compile("\\[([0-9]{10})\\][\\d]+\\s+bytes");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 1;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(i++);
            }
            return s;
        }
        else
            throw new TimeStampNotFoundException();
    }

    // Doesn't Work...
    public static String[] parseIndividualIP(String input) throws IPAddressNotFoundException {
        // Capture the ip-address after 'x byes from'
        Pattern p = Pattern.compile("bytes\\s+from\\s+([\\d,\\.]+):");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new IPAddressNotFoundException();
    }

    // Doesn't work...
    public static String[] parseIndividualICMP(String input) throws ICMPNotFoundException {
        // Capture the icmp sequence after the individual ip-address
        Pattern p = Pattern.compile("icmp_seq=(\\d+)\\s+ttl");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new ICMPNotFoundException();
    }

    // Doesn't work..
    public static String[] parseIndividualTTL(String input) throws TTLNotFoundException {
        // Capture the individual ttl after the icmp sequence
        Pattern p = Pattern.compile("ttl=(\\d+)\\s+");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TTLNotFoundException();
    }

    // Doesn't work...
    public static String[] parseIndividualTime(String input) throws TimeNotFoundException {
        // Capture the individual time after ttl
        Pattern p = Pattern.compile("time=(\\d+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TimeNotFoundException();
    }

    // Doesn't work...
    // No match found
    public static String[] parsePingStatisticsTimestamps(String input) throws TimeStampNotFoundException {
        //  Capture the first [unix-timestamp] mentioned in the ping statistics after 'ping statistics ---'
        //  Capture the second [unix-timestamp] mentioned in the ping statistics before 'rtt'
        Pattern p1 = Pattern.compile("ping\\s+statistics\\s+[\\-]+\\[([0-9]{10})\\]");
        Matcher m1 = p1.matcher(input);
        Pattern p2 = Pattern.compile("ms\\[([0-9]{10})\\]");
        Matcher m2 = p2.matcher(input);
        String[] PSTimestamps = new String[2];

        if(m1.find() || m2.find()){
            PSTimestamps[0] = m1.group(1);
            PSTimestamps[1] = m2.group(1);
            return PSTimestamps;
        }
        else
            throw new TimeStampNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsTransmitted(String input) throws TransmittedNotFoundException {
        // Capture the packets transmitted after the first [unix-timestamp]
        Pattern p = Pattern.compile("\\]([0-9]+)\\s+packets\\s+transmitted");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new TransmittedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsRecieved(String input) throws ReceivedNotFoundException {
        // Capture the packets received after 'trasnmitted,'
        Pattern p = Pattern.compile("transmitted\\,\\s+([0-9]+)\\s+receivedtransmitted\\,\\s+([0-9]+)\\s+received");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new ReceivedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsPacketLoss(String input) throws ReceivedNotFoundException {
        // Capture the packet loss % after the 'received,'
        Pattern p = Pattern.compile("loss\\,\\s+time\\s+([0-9]+)ms");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new ReceivedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsTime(String input) throws TimeNotFoundException {
        // Capture the time after 'packet loss,'
        Pattern p = Pattern.compile("rtt\\s+min\\/avg\\/max\\/mdev\\s+=\\s+([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new TimeNotFoundException();
    }

    // Does't work
    // No error but all values null
    public static String[] parsePingStatisticsMinAvgMaxMdev(String input) throws
        TimeNotFoundException {
            // Capture the rtt min/avg/max/mdev times
            Pattern p = Pattern.compile("rtt\\s+min\\/avg\\/max\\/mdev\\s+=\\s+(\\d+\\.\\d+)\\/(\\d+\\.\\d+)\\/(\\d+\\.\\d+)\\/(\\d+\\.\\d+)\\s+ms");
            Matcher m = p.matcher(input);
            if (m.find()) {
                int i = 1;
                String[] s = new String[4];
                while (m.find(i) && i <= 4) {
                    s[i - 1] = m.group(i);
                    i++;
                }
                return s;
            } else
                throw new TimeNotFoundException();
        }
    }
