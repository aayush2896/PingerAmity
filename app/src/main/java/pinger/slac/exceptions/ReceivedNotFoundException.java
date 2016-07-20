package pinger.slac.exceptions;
/**
 * Created by Rohan Sampson on 6/3/2016.
 */
public class ReceivedNotFoundException extends RMException {
    public static void printMessage(){
        System.out.println("RECIEVED STATISTICS ADDRESS NOT FOUND.");
    }
}
