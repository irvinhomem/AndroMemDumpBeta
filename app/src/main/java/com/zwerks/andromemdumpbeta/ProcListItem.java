package com.zwerks.andromemdumpbeta;

import java.util.ArrayList;

/**
 * Created by irvin on 22/02/2017.
 * Data Model for the Process List
 */

public class ProcListItem {
    private ArrayList<String> procInfoItems = new ArrayList<String>();
    private boolean isHeaderLine = false;
    private int pid = 0;
    private String proc_name = "";

    public ProcListItem(){

    }

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


}
