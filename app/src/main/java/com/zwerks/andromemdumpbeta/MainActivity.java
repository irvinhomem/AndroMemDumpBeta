package com.zwerks.andromemdumpbeta;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        //Location where the "memdump" executable should be:

        String memdumpInApkLoc = folder+ "/" + "memdump";
        String memdumpInPlaceLoc = this.getFilesDir() + "/"  + "memdump";
        //String memdumpLoc = this.getFilesDir().getParent() + "/" + "lib" +"/" + "memdump";
        // Logging
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Looking for Folder : " + folder);
            Log.d(LOG_TAG, "Checking for Memdump in: " + memdumpInPlaceLoc);
        }

        //Check if memdump executable is in the right location
        File memdumpFile = new File(memdumpInPlaceLoc);
        if(memdumpFile.exists()){
            Log.i(LOG_TAG, "Memdump file already in place");
            //Check hashes of APK asset memdump file vs. File already in place
            String inPlaceFileHash = checkFileHash(memdumpFile);
            String apkFileHash = checkFileHash(this.getAssetAsFile(memdumpInApkLoc));

            if (inPlaceFileHash.equals(apkFileHash)){
                Log.d(LOG_TAG, "Memdump files are the same ... NO NEED TO COPY");
            }
            else{
                Log.d(LOG_TAG, "Memdump files NOT the SAME ... NEED to RE-COPY");
                copyMemdumpIntoPlace(memdumpInApkLoc);
            }

        }else{
            //If not place it in the right location
            Log.i(LOG_TAG, "File NOT in place...");
            copyMemdumpIntoPlace(memdumpInApkLoc);
        }
        // Set the file on disk as executable
        this.setMemDumpFile_asExectuable(memdumpInPlaceLoc);
    }

    public void copyMemdumpIntoPlace(String inApkLocation){
        Log.i(LOG_TAG, "Starting copy process, into 'In-Place' Location ...");
        AssetManager assetManager = getAssets();
        try {
            InputStream in = assetManager.open(inApkLocation);

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
            Log.i(LOG_TAG, "Error Copying File");
            if(BuildConfig.DEBUG){
                Log.e(LOG_TAG, "Error Copying Memdump File from APK assets into App Files Directory");
                e.printStackTrace();
            }
        }
    }

    public File getAssetAsFile(String path){
        //String apkFileHash = "";
        File memDumpFileInApk = null;
        try {
            InputStream assetInputStr = getAssets().open(path);
            memDumpFileInApk = File.createTempFile("Test", "tmp");
            memDumpFileInApk.deleteOnExit();
            FileOutputStream out = new FileOutputStream(memDumpFileInApk);
            IOUtils.copy(assetInputStr,out);
            out.close();
        }catch (IOException e){
            Log.d(LOG_TAG, "IO Exception Error getting Memdump executable from APK Assets directory.");
            e.printStackTrace();
        }

        return memDumpFileInApk;
    }

    public String checkFileHash(File inputFile){
        String hash_digest_result ="";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(inputFile);

            byte[] dataBytes = new byte[4096];

            int nRead = 0;
            while ((nRead = fis.read(dataBytes)) != -1){
                md.update(dataBytes, 0, nRead);
            }

            byte[] digested_bytes = md.digest();

            //Convert Byte array to Hex format
            StringBuffer hexString =  new StringBuffer();
            for (int i = 0; i < digested_bytes.length; i++){
                hexString.append(Integer.toHexString(0xFF & digested_bytes[i]));
            }
            hash_digest_result = hexString.toString();

        }catch(Exception e){
            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            String error_msg ="";
            if(e instanceof NoSuchAlgorithmException ) {
                error_msg = "Error: Issue with the Hashing Algorithm.";
            }else if(e instanceof FileNotFoundException){
                error_msg = "Error: File Not Found.";
            }

            Log.i(LOG_TAG, error_msg);
            showToast(error_msg);
            return error_msg;
        }

        return  hash_digest_result;
    }

    public void checkRoot(View view){
        RootChecker su_check = new RootChecker(this);
        //su_check.run();
        Thread su_check_thread = new Thread(su_check, "RootCheckerThread");
        su_check_thread.start();
        //this.runOnUiThread(showToast);

    }

    /**/
    public void showToast(final String toast_text)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                //Toast myRootToast_feedback = Toast.makeText(mContext, "Root is Available.", Toast.LENGTH_LONG);
                Toast myRootToast_feedback = Toast.makeText(MainActivity.this, toast_text, Toast.LENGTH_LONG);
                myRootToast_feedback.show();
                myRootToast_feedback.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }
    /**/

    }
