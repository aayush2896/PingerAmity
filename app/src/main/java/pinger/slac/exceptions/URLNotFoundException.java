package pinger.slac.exceptions;

/**
 * Created by Rohan Sampson on 6/2/2016.
 */
public class URLNotFoundException extends RMException {
    public static void printMessage(){
        System.out.println("URL NOT FOUND.");
    }
}
