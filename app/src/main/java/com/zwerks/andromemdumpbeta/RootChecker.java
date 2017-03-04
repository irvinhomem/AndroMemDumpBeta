package com.zwerks.andromemdumpbeta;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.zwerks.andromemdumpbeta.BuildConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by irvin on 03/03/2017.
 */

public class RootChecker implements Runnable {
    private String LOG_TAG = getClass().getSimpleName();
    private MainActivity mCallingActivity;

    //BufferedReader buffRdr;

    public RootChecker(MainActivity calling_activity){
        mCallingActivity = calling_activity;
    }

    public void run(){
        checkRoot();
    }

    private void checkRoot(){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);

        if(BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "==========");
            Log.d(LOG_TAG, "Root stuff");
            Log.d(LOG_TAG, "==========");
        }
        //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su root");
        //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su -c id");
        //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su");

        try {
            Process root_proc = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(root_proc.getOutputStream());
            InputStreamReader inStream = new InputStreamReader(root_proc.getInputStream());
            BufferedReader buffRdr = new BufferedReader(inStream);

            outputStream.writeBytes("id \n");
            outputStream.flush();

            String receivingLine = null;
            /*
            receivingLine = buffRdr.readLine();
            Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
            */

            outputStream.writeBytes(mCallingActivity.getFilesDir().getPath() +"/memdump \n");
            outputStream.flush();
            /*
            receivingLine = buffRdr.readLine();
            Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
            */
            outputStream.writeBytes("uname -a \n");
            outputStream.flush();

            outputStream.writeBytes("ls -al "+ mCallingActivity.getFilesDir().getPath() +" \n");
            outputStream.flush();
            /*
            char[] receivedInput ={};
            int bufferOffset = 0;
            if(buffRdr.ready()){
                int readCount = buffRdr.read(receivedInput, bufferOffset, 1024);
            }
            Log.d(LOG_TAG, "Root Output  " + String.valueOf(receivedInput.length) + String.valueOf(receivedInput));
            */

            while((receivingLine = buffRdr.readLine()) != null) {
                //buffRdr.lines() ".lines only avaiable in API 24 onwards"
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
                }
                //receivingLine.matches("uid=0|gid=0"
                //if (receivingLine.contains("uid=0")){
                if (receivingLine.matches(".*uid=0.*|.*gid=0.*")){
                    //Using Regex or string.contains method above

                    mCallingActivity.showToast("Root / SU is available ...");
                    /*BELOW: ACTUALLY WORKS ALSO*/
                    /*
                    mCallingActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mCallingActivity, "Hello", Toast.LENGTH_SHORT).show();
                        }
                    });
                    */
                }
            }
            /**/
            buffRdr.close();
            /**/
            try {
                root_proc.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG, "Caught Interrupted Exception");
            }
            /**/
        }catch(IOException e){
            Log.d(LOG_TAG, "Root didn't work: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
