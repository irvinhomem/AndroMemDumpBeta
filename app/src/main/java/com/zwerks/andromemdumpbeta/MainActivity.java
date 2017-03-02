package com.zwerks.andromemdumpbeta;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = getClass().getSimpleName(); //private final String
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        //System.loadLibrary("memdump");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Check MemDump Location
        check_MemDumpExecutableLocation();

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Method for opening the ProcessesListing Activity
    public void openProcessesListing(View view){
        Intent intent =  new Intent(this, ListProcessesActivity.class);
        startActivity(intent);
    }

    public boolean setMemDumpFile_asExectuable(String fileLoc){
        /**/
        File execFile = new File(fileLoc);
        //Somewhat Safe
        //boolean exec_success = execFile.setExecutable(true);
        //*** UNSAFE *** !! NEED TO FIND A BETTER SOLUTION
        boolean exec_success = execFile.setExecutable(true, false);
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Set file to executable = " + exec_success);
        }
        /**/
        return exec_success;
    }

    public void check_MemDumpExecutableLocation(){
        //Check ABI (Android Binary Interface) compatibility with CPU / API Levels
        String abi;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            abi = Build.SUPPORTED_ABIS[0];
        }else{
            //noinspection deprecation
            abi = Build.CPU_ABI; // Deprecated from API level 21 and onwards
        }
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "ABI : " + abi);
        }

        String folder = "";
        if(abi.contains("armeabi-v7a")){
            folder = "armeabi-v7a";
        }else if(abi.contains("arm64-v8a")){
            folder = "arm64-v8a";
        }else if(abi.contains("x86_64")){
            folder = "x86_64";
        } else if (abi.contains("x86")) {
            folder = "x86";
        } else if (abi.contains("armeabi")) {
            folder = "armeabi";
        }
        String memdumpLoc = this.getFilesDir() + "/"  + "memdump";
        //String memdumpLoc = this.getFilesDir().getParent() + "/" + "lib" +"/" + "memdump";
        // Logging
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Looking for Folder : " + folder);
            Log.d(LOG_TAG, "Checking for Memdump in: " + memdumpLoc);
        }

        //Check if memdump executable is in the right location
        File memdumpFile = new File(memdumpLoc);
        if(memdumpFile.exists()){
            Log.d(LOG_TAG, "Memdump file already in place");
        }else{
            //If not place it in the right location
            Log.i(LOG_TAG, "File NOT in place. Starting copy process ...");
            AssetManager assetManager = getAssets();
            try {
                InputStream in = assetManager.open(folder + "/" + "memdump");

                //Copy file to appropriate location
                //this = context
                //this.openFileOutput <-- Directly puts the file into the "/files" directory
                OutputStream out = this.openFileOutput("memdump", MODE_PRIVATE);
                long size = 0;
                int nRead;
                byte[] buff = new byte[50000];

                while((nRead = in.read(buff)) != -1){
                    out.write(buff, 0 , nRead);
                    size += nRead;
                }
                out.flush();
                Log.d(LOG_TAG, "Copy Success: " + size + " bytes");

            }catch(IOException e){

            }
        }
        this.setMemDumpFile_asExectuable(memdumpLoc);


    }

    public void checkRoot(View view){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        try {
            if(BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "==========");
                Log.d(LOG_TAG, "Root stuff");
                Log.d(LOG_TAG, "==========");
            }
            //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su root");
            //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su -c id");
            //Process root_proc = Runtime.getRuntime().exec("/system/xbin/su");
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
            outputStream.writeBytes(this.getFilesDir().getPath() +"/memdump \n");
            outputStream.flush();
            /*
            receivingLine = buffRdr.readLine();
            Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
            */
            outputStream.writeBytes("uname -a \n");
            outputStream.flush();

            outputStream.writeBytes("ls -al "+ this.getFilesDir().getPath() +" \n");
            outputStream.flush();
            /*
            char[] receivedInput ={};
            int bufferOffset = 0;
            if(buffRdr.ready()){
                int readCount = buffRdr.read(receivedInput, bufferOffset, 1024);
            }
            Log.d(LOG_TAG, "Root Output  " + String.valueOf(receivedInput.length) + String.valueOf(receivedInput));
            */
            /**/
            while((receivingLine = buffRdr.readLine()) != null) {
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
                }
            }
            /**/
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
