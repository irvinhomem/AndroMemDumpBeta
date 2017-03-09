package com.zwerks.andromemdumpbeta;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class ListProcessesActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    // RecyclerView variables
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private ProcessRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mHeaderLineText;
    private AppCompatEditText mTxtFilterCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_processes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mHeaderLineText = (TextView) findViewById(R.id.txt_HeaderTextLine);

        /* Use this setting to improve performance if you know that changes
         in content do not change the layout size of the RecyclerView */
        mRecyclerView.setHasFixedSize(true);

        // Use a Linear Layout Manager for the RecyclerView
        mLayoutManager =  new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Specify an adapter
        psProcessLister psLister = new psProcessLister(this);
        this.mAdapter = new ProcessRecyclerAdapter(this, psLister.getProcessesAsList());
        this.mRecyclerView.setAdapter(mAdapter);

        // Get the Headers from the Headers ArrayList
        ProcListItem headersLineItem = (ProcListItem)psLister.getHeadersArrayList().get(0);
        String headersAsString = headersLineItem.getAllItems();
        mHeaderLineText.setText(headersAsString);

        //Populate Spinner
        if(psLister.getHeadersArrayList().size() > 0) {
            ArrayList<String> headers = psLister.getHeadersArrayList().get(0).getProcInfoItems();
            String[] headersArray = headers.toArray(new String[0]);

            ArrayAdapter<String> spinnerAdapter =  new ArrayAdapter<String>(this, R.layout.process_filter_spinner_item, headersArray);

            spinnerAdapter.setDropDownViewResource(R.layout.process_filter_spinner_dropdown_item);
            Spinner filterOptions = (Spinner) findViewById(R.id.spnFilterOptions);
            filterOptions.setAdapter(spinnerAdapter);
            filterOptions.setSelection(spinnerAdapter.getPosition("NAME"));

        }else{
            Log.d(LOG_TAG, "Header Strings missing in ProcItems ArrayList");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // psProcessLister procLister = new psProcessLister();
        // String processList = procLister.getProcessListOutput();
        //String processList =  new psProcessLister().getProcessListOutput();
        TextView txtProcessList = (TextView) findViewById(R.id.txt_process_list_output);
        //txtProcessList.setText(new psProcessLister().getProcessListOutput());
        txtProcessList.setText(String.valueOf(psLister.getProcessListSize()));

        TextView txtProcCount = (TextView) findViewById(R.id.txt_procCount);
        txtProcCount.setText(String.valueOf(psLister.getProcessListSize()));

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    public void doBroadFilter(View view){

        mTxtFilterCriteria = (AppCompatEditText)findViewById(R.id.txtFilterCriteria);
        String filterCriteria = mTxtFilterCriteria.getText().toString();
        this.mAdapter.filter("nothing yet", filterCriteria);

        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG, "Current Text: " + filterCriteria);
        }
    }
}
