package com.zwerks.andromemdumpbeta;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by irvin on 21/02/2017.
 */

public class psProcessLister {
    private String LOG_TAG = getClass().getSimpleName();

    public psProcessLister(){

    }

    public String getProcessListOutput(){
        String psProcess_output = "Processes: \n";
        try{
            Process myProcess = Runtime.getRuntime().exec("/system/bin/ps");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/toolbox ps");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/toybox ps");

            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof com.zwerks.andromemdumpbeta");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof com.android.calendar");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof com.android.keychain");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pidof -s com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/pgrep -f com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/ps | grep com.android.chrome");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/ps | grep calendar");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/ps | grep zwerks");
            //Process myProcess = Runtime.getRuntime().exec("/system/bin/ps | /system/bin/grep zwerks");
            //Process myProcess = Runtime.getRuntime().exec("adb shell ls");

            InputStreamReader inStreamRdr = new InputStreamReader(myProcess.getInputStream());
            //Log.d(LOG_TAG, "InputStream data available: " + inStreamRdr.ready());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);
            //Log.d(LOG_TAG, "InputStream data available: " + buffReader.ready());

            int numRead; // The bufferedReader returns the number of characters read
            char[] destBuffer = new char[5000];
            StringBuffer commandOutput = new StringBuffer();

            // "buffreader.read" returns the number of characters read from the buffReader into the destBuffer
            while ((numRead = buffReader.read(destBuffer)) > 0){
                // The StringBuffer "commandOutput" receives the Char array [buffer] and appends it to itself
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "BuffReader has something: " + String.valueOf(numRead));
                }
                commandOutput.append(destBuffer, 0, numRead);
            }
            buffReader.close();

            try {
                myProcess.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG,"Caught InterruptedException", new RuntimeException(e));
            }

            psProcess_output += commandOutput.toString();

        }catch (IOException e){
            Log.e(LOG_TAG,"Caught IO Exception", new RuntimeException(e));
        }

        return psProcess_output;
    }

    public ArrayList getProcessesAsList(){
        ArrayList<String> processArrayList = new ArrayList<>();

        try{
            Process myProcess = Runtime.getRuntime().exec("/system/bin/ps");

            InputStreamReader inStreamRdr = new InputStreamReader(myProcess.getInputStream());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);

            //while(buffReader.readLine() != null)  //Misses the first line
            while(buffReader.read() > 0){
                processArrayList.add(buffReader.readLine());
                if(BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "'Process' Items in ArrayList: " + String.valueOf(processArrayList.size()));
                }
            }
            buffReader.close();

            try {
                myProcess.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG,"Caught InterruptedException", new RuntimeException(e));
            }
        }catch(IOException e){
            Log.e(LOG_TAG,"Caught IO Exception", new RuntimeException(e));
        }

        // Extra Debug Logging / Testing code
        if(BuildConfig.DEBUG) {
            if (processArrayList.size() > 0 ) {
                Log.d(LOG_TAG, "1st Item (ArrayList): " + String.valueOf(processArrayList.get(0)));
            }
        }

        return processArrayList;
    }
}
