package com.zwerks.andromemdumpbeta;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by irvin on 21/02/2017.
 */

public class psProcessLister {
    private String LOG_TAG = getClass().getSimpleName();

    public psProcessLister(){

    }

    public String listProcesses(){
        String process_list = "Processes: \n";
        try{
            Process myProcess = Runtime.getRuntime().exec("/system/bin/ps");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/toolbox ps");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/toybox ps");

            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pgrep -f com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/ps | grep com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("adb shell ls");

            InputStreamReader inStreamRdr = new InputStreamReader(myProcess.getInputStream());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);

            int numRead; // The bufferedReader returns the number of characters read
            char[] destBuffer = new char[5000];
            StringBuffer commandOutput = new StringBuffer();

            // "buffreader.read" returns the number of characters read from the buffReader into the destBuffer
            while ((numRead = buffReader.read(destBuffer)) > 0){
                // The StringBuffer "commandOutput" receives the Char array [buffer] and appends it to itself
                commandOutput.append(destBuffer, 0, numRead);
            }
            buffReader.close();

            try {
                myProcess.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG,"Caught InterruptedException", new RuntimeException(e));
            }

            process_list += commandOutput.toString();

        }catch (IOException e){
            Log.e(LOG_TAG,"Caught IO Exception", new RuntimeException(e));
        }

        return process_list;
    }
}
