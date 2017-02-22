package com.zwerks.andromemdumpbeta;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ListProcessesActivity extends AppCompatActivity {

    // RecyclerView variables
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_processes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        /* Use this setting to improve performance if you know that changes
         in content do not change the layout size of the RecyclerView */
        mRecyclerView.setHasFixedSize(true);

        // Use a Linear Layout Manager for the RecyclerView
        mLayoutManager =  new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Specify an adapter
        psProcessLister psLister = new psProcessLister();
        mAdapter = new ProcessRecyclerAdapter(psLister.getProcessesAsList(), psLister.getHeadersArrayList());
        mRecyclerView.setAdapter(mAdapter);

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
        txtProcessList.setText(String.valueOf(new psProcessLister().getProcessesAsList().size()));
    }
}
