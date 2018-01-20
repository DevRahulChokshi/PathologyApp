package com.example.ebc003.pathologyapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * Created by EBC003 on 11/21/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<AppointmentData> listData;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter (Context context, List<AppointmentData> listData) {
        layoutInflater=LayoutInflater.from (context);
        this.listData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate (R.layout.list_appointment_cardview,parent,false);
        return new MyViewHolder (view);
    }


    @Override
    public void onBindViewHolder (MyViewHolder holder, int position) {
        if (listData!=null){
            for (int i=0;i<=listData.size ();i++){
                AppointmentData appointmentData=listData.get (position);
                holder.mUserName.setText (appointmentData.getUserName ());
                holder.mUserName.setText (appointmentData.getUserName ());
                holder.mTestName.setText (appointmentData.getTest_type_Name ());
                holder.mSchedule.setText (appointmentData.getSchedule ());
                holder.mTime.setText (appointmentData.getTime ());
                Log.i (RecyclerViewAdapter.class.getSimpleName (),"DATA LIST IS NOT NULL");
            }

        }else {
            Log.i (RecyclerViewAdapter.class.getSimpleName (),"DATA LIST IS NULL");
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount () {
        return listData.size ();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mUserName;
        TextView mTestName;
        TextView mSchedule;
        TextView mTime;



        public MyViewHolder (View itemView) {
            super (itemView);

            mUserName=itemView.findViewById (R.id.txtUserName);
            mTestName=itemView.findViewById (R.id.txtTestType);
            mSchedule=itemView.findViewById (R.id.txtSchedule);
            mTime=itemView.findViewById (R.id.txtTime);
        }
    }
}
