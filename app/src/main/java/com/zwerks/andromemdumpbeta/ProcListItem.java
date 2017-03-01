package com.zwerks.andromemdumpbeta;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


/**
 * Created by irvin on 22/02/2017.
 * Data Model for the Process List
 */

public class ProcListItem {
    private final String LOG_TAG = getClass().getSimpleName();
    private Context mContext;

    private ArrayList<String> procInfoItems = new ArrayList<String>();
    private boolean isHeaderLine = false;
    private int pid = 0;
    private String proc_name = "";

    public ProcListItem(Context context){
        mContext = context;
    }
    /*public ProcListItem(){

    }
    */

    public void append(String procInfoItem){
        this.procInfoItems.add(procInfoItem);
    }

    public String getAllItems(){
        String allProcInfo = "";
        for(int i = 0; i <= procInfoItems.size()-1 ;i++){

            allProcInfo += (procInfoItems.get(i) + "\t"); // "\t\t|\t"
        }

        return allProcInfo;
    }

    public int getPid(){
        if (procInfoItems.size() > 0){
            this.pid = Integer.parseInt(procInfoItems.get(1));
        }
        return this.pid;
    }

    public String getProc_name(){
        if (procInfoItems.size() > 0){
            String unfiltered_name = procInfoItems.get(procInfoItems.size()-1);
            //Use RegEx to remove special characters except the "." ... i.e maintaining alphanumeric and "."
            this.proc_name = unfiltered_name.replaceAll("[^a-zA-Z0-9\\.]+","_");
        }
        return this.proc_name;
    }

    public void setHeaderLine(boolean bool){
        this.isHeaderLine = bool;
    }

    public boolean getIsHeaderLine(){
        return this.isHeaderLine;
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
        //String dumpWriteLoc = mContext.getExternalMediaDirs().toString();
        if(BuildConfig.DEBUG){
            //Log.d(LOG_TAG, "Dump Location: " + dumpLocation );
            //Log.d(LOG_TAG, "Dump Location: " + dumpWriteLocPath ); //SDCard root location
            Log.d(LOG_TAG, "MemDump Executable Location: " + memDumpExecLoc);
            Log.d(LOG_TAG, "Potential Dump OUTPUT Location: " + dumpWriteLocPath.getPath());
        }

        /**/
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
            dumpFileName = this.getProc_name();
            dumpFileName += ".dmp";

            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid()) + " > " + dumpWriteLocPath.getPath()+ "/"+ dumpFileName;
            //String[] memdump_Command = {memDumpExecLoc, String.valueOf(this.getPid()), " \\> ", dumpWriteLocPath.getPath()+ "/"+ dumpFileName};
            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid()) + " > " + dumpFileName;
            String[] memdump_Command = {memDumpExecLoc, String.valueOf(this.getPid())};
            //String memdump_Command = memDumpExecLoc + " " + String.valueOf(this.getPid());

            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, "Dump FileName: " + dumpFileName);
                //Log.d(LOG_TAG, "MemDump Command: " + memdump_Command);
                Log.d(LOG_TAG, "MemDump Command: " + Arrays.toString(memdump_Command));
            }

            try {
                Process root_proc = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(root_proc.getOutputStream());
                InputStreamReader inStream = new InputStreamReader(root_proc.getInputStream());
                BufferedReader buffRdr = new BufferedReader(inStream);

                outputStream.writeBytes("id");
                outputStream.flush();

                String receivingLine;
                while((receivingLine = buffRdr.readLine()) != null) {
                    if(BuildConfig.DEBUG){
                        Log.d(LOG_TAG, "Root Output Line: " + String.valueOf(receivingLine.length()) + String.valueOf(receivingLine));
                    }
                }
                buffRdr.close();
                root_proc.waitFor();
            }catch(Exception e){
                Log.d(LOG_TAG, "Root didn't work: " + e.getMessage());
            }


            Process dumpingProcess = Runtime.getRuntime().exec(memdump_Command);
            //Process dumpingProcess = Runtime.getRuntime().exec(memDumpExecLoc);
            //Process dumpingProcess = Runtime.getRuntime().exec("memdump " + String.valueOf(this.getPid()) + dumpLocation);

            InputStreamReader inStreamRdr = new InputStreamReader(dumpingProcess.getInputStream());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);

            String singleLine;
            while((singleLine = buffReader.readLine()) != null) {
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Dump Line: " + String.valueOf(singleLine.length()) + String.valueOf(singleLine));
                }
            }
            buffReader.close();
            try {
                dumpingProcess.waitFor();
            }catch (InterruptedException e){
                Log.e(LOG_TAG,"Caught InterruptedException", new RuntimeException(e));
            }

        }catch(IOException e){

        }
        /**/
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "***********************");
            Log.d(LOG_TAG, "END OF Memdump ...");
            Log.d(LOG_TAG, "***********************");
        }

    }


}
