package pinger.slac.exceptions;

/**
 * Created by Rohan Sampson on 6/3/2016.
 */
public class ICMPNotFoundException extends RMException {
    public static void printMessage(){
        System.out.println("ICMP SEQUENCE ADDRESS NOT FOUND.");
    }
}
