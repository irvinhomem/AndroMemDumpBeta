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

    public void dumpProcMem(){
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Preparing MemDumper ...");
        }
        MemDumper mem_dumper =  new MemDumper(this.mContext, this);
        Thread myDumpThread = new Thread(mem_dumper, "Proc_MemDumper");
        myDumpThread.start();

        //mem_dumper.run();
    }




}
