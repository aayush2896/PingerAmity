package pinger.slac.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches
{

    public static String PassesAllTests(String pingData){
        String finalString="";
        //Try all the checks here and extract the necessary data using the regex below pingData is one block of ping data i.e always complete either you get all the data or you get nothing ensuring consistency in the result

        return finalString;
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
        if (m.find())
            return m.group(1);
        else
            throw new TimeStampNotFoundException();
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
        Matcher m2 = p2.matcher(input);
        String[] GroupBytes = new String[2];
        int x = m1.groupCount();
        int y = m2.groupCount();
        if(m1.find() || m2.find()){
            GroupBytes[0] = m1.group(1);
            GroupBytes[1] = m2.group(1);
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
    public static String[] parsePingStatisticsMinAvgMaxMdev(String input) throws TimeNotFoundException {
        // Capture the rtt min/avg/max/mdev times
        Pattern p = Pattern.compile("rtt\\s+min/avg/max/mdev\\s+=\\s+([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[4];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TimeNotFoundException();

    }
}