package pinger.slac.exceptions;

/**
 * Created by Rohan Sampson on 5/30/2016.
 */
public class TimeStampNotFoundException extends RMException {
    public static void printMessage(){
        System.out.println("UNIX TIMESTAMP NOT FOUND.");
    }
}
