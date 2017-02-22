package com.zwerks.andromemdumpbeta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by irvin on 22/02/2017.
 */

public class ProcessRecyclerAdapter extends RecyclerView.Adapter<ProcessRecyclerAdapter.ViewHolder> {
    //private ArrayList<String> mDataset;     // Variable with all the processes
    private ArrayList<ProcListItem> mDataset;     // Variable with all the processes
    //private ArrayList<ProcListItem> mHeadersLine;
    //private Context mContext;               // Store the context for easy access
    private RadioButton lastCheckedRB = null;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public TextView mTextView;
        public RadioButton mRadioButton;
        public Button msgButton; // Put a Button for fun ... to test each item

        public ViewHolder(View view) {
            super(view);
            //mTextView = v;
            //mTextView = (TextView) view.findViewById(R.id.single_process_data);
            mRadioButton = (RadioButton) view.findViewById(R.id.single_process_data);
            msgButton = (Button) view.findViewById(R.id.message_button);

            //mTextView.setTextSize(10);
            mRadioButton.setTextSize(10);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    //public ProcessRecyclerAdapter(ArrayList<String> myDataset) {
    //public ProcessRecyclerAdapter(ArrayList<ProcListItem> myDataset, ArrayList<ProcListItem> headers) {
    public ProcessRecyclerAdapter(ArrayList<ProcListItem> myDataset) {
        //Assign the received dataset (arraylist) to the adapter's dataset property
        mDataset = myDataset;
        //this.mHeadersLine = headers;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProcessRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //View processView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Inflate the custom layout
        View processView = inflater.inflate(R.layout.process_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(processView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //holder.mTextView.setText(String.valueOf(mDataset.get(position)));
        // Extract info items
        ProcListItem procItem = mDataset.get(position);
        holder.mRadioButton.setText(String.valueOf(procItem.getAllItems()));
        //holder.mRadioButton.setText(String.valueOf(mDataset.get(position)));
        if(procItem.getIsHeaderLine()){
            //holder.mRadioButton.setVisibility();
        }else {
            int proc_id = procItem.getPid();
            String proc_name = procItem.getProc_name();
            //holder.mRadioButton.setTag(proc_id, proc_name);
            holder.mRadioButton.setTag(proc_name);
            holder.msgButton.setText("Dump");
        }

        // Code to ensure only a single Radio Button / Process item is selected
        View.OnClickListener rbClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton curr_checked_rb = (RadioButton) view;
                if(lastCheckedRB != null){
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = curr_checked_rb;
            }
        };
        holder.mRadioButton.setOnClickListener(rbClick);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
