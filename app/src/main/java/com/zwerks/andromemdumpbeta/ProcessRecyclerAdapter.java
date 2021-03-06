package com.zwerks.andromemdumpbeta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private final String LOG_TAG = getClass().getSimpleName();
    //private ArrayList<String> mDataset;     // Variable with all the processes
    private ArrayList<ProcListItem> mDataset;     // Variable with all the processes
    private ArrayList<ProcListItem> itemsCopy;
    //private ArrayList<ProcListItem> mHeadersLine;
    //private Context mContext;               // Store the context for easy access
    private ViewGroup mLastClickedRow_rb_btn = null;
    // Store the context for easy access
    private Context mContext;

    //For accessing the memdump library
    static {
        //System.loadLibrary("memdump");
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public TextView mTextView;
        public RadioButton mRadioButton;
        public Button btnDumpProc; // Put a Button for fun ... to test each item

        public ViewHolder(View view) {
            super(view);
            //mTextView = v;
            //mTextView = (TextView) view.findViewById(R.id.single_process_data);
            mRadioButton = (RadioButton) view.findViewById(R.id.rb_single_process_data);
            btnDumpProc = (Button) view.findViewById(R.id.btn_DumpProc);

            //mTextView.setTextSize(10);
            mRadioButton.setTextSize(10);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    //public ProcessRecyclerAdapter(ArrayList<String> myDataset) {
    //public ProcessRecyclerAdapter(ArrayList<ProcListItem> myDataset, ArrayList<ProcListItem> headers) {
    public ProcessRecyclerAdapter(Context context, ArrayList<ProcListItem> myDataset) {
        //Assign the received dataset (arraylist) to the adapter's dataset property
        mDataset = myDataset;
        mContext = context;

        itemsCopy = new ArrayList<ProcListItem>();
        itemsCopy.addAll(mDataset);
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
        final ProcListItem procItem = mDataset.get(position);
        holder.mRadioButton.setText(String.valueOf(procItem.getAllItems()));
        //holder.mRadioButton.setText(String.valueOf(mDataset.get(position)));
        if(procItem.getIsHeaderLine()){
            //holder.mRadioButton.setVisibility();
        }else {
            int proc_id = procItem.getPid();
            String proc_name = procItem.getProc_name();
            //holder.mRadioButton.setTag(proc_id, proc_name);
            //holder.mRadioButton.setTag(proc_id);
            //holder.mRadioButton.setTag(proc_name);
            holder.mRadioButton.setTag(procItem);
            holder.btnDumpProc.setText("Dump");
            //holder.btnDumpProc.setVisibility(View.INVISIBLE);
            holder.btnDumpProc.setVisibility(View.GONE);
        }

        // Code to ensure only a single Radio Button / Process item is selected
        View.OnClickListener rbClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get Parent view of clicked item, so as to get Dump button and set visible
                ViewGroup latest_click_row = (ViewGroup) view.getParent();

                //RadioButton curr_checked_rb = (RadioButton) view;
                //Check if there is already a View selected (i.e. a ViewGroup clicked before, and thus stored)
                if(mLastClickedRow_rb_btn != null){
                    //If yes "unselect" the old one in preparation to select the latest selected one
                    RadioButton rb_LastClicked = (RadioButton) mLastClickedRow_rb_btn.findViewById(R.id.rb_single_process_data);
                    Button btn_LastClicked = (Button)mLastClickedRow_rb_btn.findViewById(R.id.btn_DumpProc);
                    rb_LastClicked.setChecked(false);
                    btn_LastClicked.setVisibility(View.GONE);
                }
                // Store the currently clicked View
                mLastClickedRow_rb_btn = latest_click_row;
                RadioButton rbCurrSelected = (RadioButton) latest_click_row.findViewById(R.id.rb_single_process_data);
                rbCurrSelected.setChecked(true);
                Button btnDump_selected = (Button) latest_click_row.findViewById(R.id.btn_DumpProc);
                btnDump_selected.setVisibility(View.VISIBLE);
            }
        };
        holder.mRadioButton.setOnClickListener(rbClick);

        // Code to add onClickListener for the "Dump process" button
        View.OnClickListener btnDumpProc_click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for currently clicked Radiobutton and check for "Tag"
                ViewGroup row = (ViewGroup) view.getParent();
                RadioButton rb_selected = (RadioButton) row.findViewById(R.id.rb_single_process_data);
                if (rb_selected.isChecked()){
                    if(BuildConfig.DEBUG){
                        ProcListItem proc_item = (ProcListItem)rb_selected.getTag();
                        Log.d(LOG_TAG, "RB-Selected Tag: " + String.valueOf(proc_item.getPid()) +" - "+ proc_item.getProc_name() );
                    }
                    //Dump Process Memory
                    procItem.dumpProcMem();
                    //Proceed to dump the selected process
                } else{ //Throw a dialog to tell the user to select the right process, or a process.

                }
                //If none, show dialog that no process has been selected
                //Else ask for dump location and start dumping
                //RadioButton selected_rb = (RadioButton) view;
            }
        };
        holder.btnDumpProc.setOnClickListener(btnDumpProc_click);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void filter(String option, String filterCriteria){
        mDataset.clear();
        if(filterCriteria.isEmpty()){
            mDataset.addAll(itemsCopy);
        }else{
            String filterCriterion = filterCriteria.toLowerCase();
            for (ProcListItem procItem: itemsCopy){
                for (int i =0; i < procItem.getProcInfoItems().size(); i++){
                    if(procItem.getProcInfoItems(i).toLowerCase().contains(filterCriterion)){
                        mDataset.add(procItem);
                    }
                }

            }
        }
        notifyDataSetChanged();

    }
/*
    public dumpProcessMemory(ProcListItem processItem){
        processItem.dumpMemory();
    }
*/
}
