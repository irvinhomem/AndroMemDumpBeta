package com.zwerks.andromemdumpbeta;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
            this.proc_name = procInfoItems.get(procInfoItems.size()-1);
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
        String dumpLocation = mContext.getFilesDir().getPath() + "/";
        String memDumpExec = mContext.getFilesDir().getParent() + "/lib/" + "libmemdump.so";
        //mContext.getApplicationInfo().nativeLibraryDir;
        //String memDumpExec = mContext.getApplicationInfo().nativeLibraryDir + "/" + "libmemdump.so";
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Dump Location: " + dumpLocation );
            Log.d(LOG_TAG, "MemDump Executable Location: " + memDumpExec);
        }

        /**/
        try {
            if(BuildConfig.DEBUG){
                Log.d(LOG_TAG, "About to start Memdump ...");
            }
            Process dumpingProcess = Runtime.getRuntime().exec(memDumpExec);

            //Process dumpingProcess = Runtime.getRuntime().exec("memdump " + String.valueOf(this.getPid()) + dumpLocation);

            InputStreamReader inStreamRdr = new InputStreamReader(dumpingProcess.getInputStream());
            BufferedReader buffReader = new BufferedReader(inStreamRdr);

            String singleLine;
            while((singleLine = buffReader.readLine()) != null) {
                if(BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "Dump Line Length;" + String.valueOf(singleLine.length()));
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

    }


}
