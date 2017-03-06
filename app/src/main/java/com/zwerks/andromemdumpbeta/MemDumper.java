package com.zwerks.andromemdumpbeta;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static android.os.Build.VERSION.SDK;

/**
 * Created by irvin on 04/03/2017.
 */

public class MemDumper implements Runnable {
    private String LOG_TAG = getClass().getSimpleName();
    private Context mContext;
    private ProcListItem mProcItem;

    public MemDumper(Context context, ProcListItem procListItem){
        mContext = context;
        mProcItem = procListItem;
    }

    @Override
    public void run() {
        if(BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "_________________");
            Log.d(LOG_TAG, "Preparing for Dump ...");
            Log.d(LOG_TAG, "-----------------");
        }
        dumpProcessMemory();
    }

    public void dumpProcessMemory(){
        //this.getPid();
        //String dumpLocation = "/data/data/" + getClass().getPackage().getName();
        //String dumpLocation = mContext.getFilesDir().getPath() + mContext.getPackageName() + "/";
        //String memdump_executable = "libmemdump.so";
        String memdump_executable = "memdump";

        //String dumpLocation = mContext.getFilesDir().getPath() + "/";
        //String memDumpExec = mContext.getFilesDir().getParent() + "/lib/" + memdump_executable;
        String memDumpExecLoc = mContext.getFilesDir() + "/" + memdump_executable;
        //mContext.getApplicationInfo().nativeLibraryDir;
        //String memDumpExec = mContext.getApplicationInfo().nativeLibraryDir + "/" + "libmemdump.so";

        File dumpWriteLocPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        /*
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Ext Dir #: " + dumpWriteLocPath.getPath());
        }
        */
        /*
        String locations;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] dumpWriteLocPath = mContext.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
            locations = Arrays.toString(dumpWriteLocPath);
            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, "Locations #: " + String.valueOf(dumpWriteLocPath.length));
                Log.d(LOG_TAG, "Locations >= API 19: " + locations);
            }
        } else{
            List<StorageUtils.StorageInfo> StorageLocations = StorageUtils.getStorageList();
            locations = StorageLocations.toString();
            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG,"Locations < API 19: " + locations);
            }
        }
        */

        //String dumpWriteLoc = mContext.getExternalMediaDirs().toString();
        if(BuildConfig.DEBUG){
            //Log.d(LOG_TAG, "Dump Location: " + dumpLocation );
            //Log.d(LOG_TAG, "Dump Location: " + dumpWriteLocPath ); //SDCard root location
            Log.d(LOG_TAG, "MemDump Executable Location: " + memDumpExecLoc);
            Log.d(LOG_TAG, "Potential Dump OUTPUT Location: " + dumpWriteLocPath.getPath());
        }

        this.checkExternalMedia();

        try {
            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, "===================================");
                Log.d(LOG_TAG, "About to start Memdump ...");
                Log.d(LOG_TAG, "===================================");
            }

            //Process dumpingProcess = Runtime.getRuntime().exec(memDumpExec);
            String dumpFileName = "";
            /*
            String[] proc_names = this.getProc_name().split("[.]");
            Log.d(LOG_TAG, "ProcName: "+ this.getProc_name());
            Log.d(LOG_TAG, "Number of name splits: " + proc_names.length);
            if (proc_names.length > 1){
                dumpFileName = proc_names[proc_names.length-2] + "." + proc_names[proc_names.length-1];
            }else{
                dumpFileName = this.getProc_name();
            }
            */
            dumpFileName = mProcItem.getProc_name();
            dumpFileName += ".dmp";

            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid()) + " > " + dumpWriteLocPath.getPath()+ "/"+ dumpFileName;
            //String[] memdump_Command = {memDumpExecLoc, String.valueOf(this.getPid()), " \\> ", dumpWriteLocPath.getPath()+ "/"+ dumpFileName};
            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid()) + " > " + dumpFileName;
            String[] memdump_Command = {memDumpExecLoc, String.valueOf(mProcItem.getPid())}; // Will handle Redirection of StdIn from Memdump to store as file afterwards
            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid());

            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, "Dump FileName: " + dumpFileName);
                //Log.d(LOG_TAG, "MemDump Command: " + memdump_Command);
                Log.d(LOG_TAG, "MemDump Command: " + Arrays.toString(memdump_Command));
            }

            /**/
            Process dumpingProcess = Runtime.getRuntime().exec(memdump_Command);
            //Process dumpingProcess = Runtime.getRuntime().exec(memDumpExecLoc);
            //Process dumpingProcess = Runtime.getRuntime().exec("memdump " + String.valueOf(this.getPid()) + dumpLocation);

            InputStreamReader inStreamRdr = new InputStreamReader(dumpingProcess.getInputStream());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);
            ////ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            /**/
            File dumpOutputFileDir = new File(dumpWriteLocPath.getPath() + "/MEMORY_DUMPS/");
            //Check if Dir already exists
            boolean dirSuccess = dumpOutputFileDir.mkdir();
            File dumpOutputFile = new File(dumpOutputFileDir, dumpFileName);
            //dumpOutputFile.createNewFile();
            FileWriter dumpFile =  new FileWriter(dumpOutputFile);
            /**/

            //Reading out as lines
            /*
            String singleLine;
            while((singleLine = buffReader.readLine()) != null) {
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Dump Line: " + String.valueOf(singleLine.length()) + String.valueOf(singleLine));
                }
                dumpFile.write(singleLine);
            }
            */
            /**/
            char[] aCharBuff = new char[4096];
            double charCounter = 0;
            int charsRead;
            while((charsRead = buffReader.read(aCharBuff, 0, aCharBuff.length)) != -1){
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Got Data: " + String.valueOf(aCharBuff));
                }
                dumpFile.write(aCharBuff);
                //dumpFile.write(aCharBuff, 0, charsRead);
                //Keeping track of "Write" progress
                charCounter++;
            }
            Log.i(LOG_TAG, "File Size: " + charCounter);
            /**/

            /**/
            //buffReader.close();
            try {
                dumpingProcess.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG,"Caught InterruptedException", new RuntimeException(e));
            }
            /**/

        }catch(IOException e){

        }
        /**/
        /**/
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "***********************");
            Log.d(LOG_TAG, "END OF Memdump ...");
            Log.d(LOG_TAG, "***********************");
        }
            /**/

    }

    private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        Log.i(LOG_TAG, "External Media: readable="+mExternalStorageAvailable+" writable="+mExternalStorageWriteable);
    }
}
