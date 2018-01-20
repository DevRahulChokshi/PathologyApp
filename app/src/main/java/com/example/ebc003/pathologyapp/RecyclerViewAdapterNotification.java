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
 * Created by EBC003 on 12/6/2017.
 */

public class RecyclerViewAdapterNotification extends RecyclerView.Adapter<RecyclerViewAdapterNotification.MyViewHolder> {

    private List<AppointmentData> listData;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapterNotification (Context context,List<AppointmentData> listData) {
        layoutInflater=LayoutInflater.from (context);
        this.listData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate (R.layout.list_notification_cardview,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder, int position) {
        if (listData!=null){
            for (int i=0;i<=listData.size ();i++){
                AppointmentData appointmentData=listData.get (position);
                holder.mTestType.setText (appointmentData.getUserName ());
                holder.mTimeStamp.setText (appointmentData.getTest_type_Name ());
                holder.mTestDetails.setText (appointmentData.getSchedule ());
                Log.i (RecyclerViewAdapterNotification.class.getSimpleName (),"DATA LIST IS NOT NULL");
            }

        }else {
            Log.i (RecyclerViewAdapterNotification.class.getSimpleName (),"DATA LIST IS NULL");
        }
    }



    @Override
    public int getItemCount () {
        return listData.size ();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTestType;
        TextView mTimeStamp;
        TextView mTestDetails;

        public MyViewHolder (View itemView) {
            super (itemView);

            mTestType =itemView.findViewById (R.id.card_view_test_type);
            mTimeStamp =itemView.findViewById (R.id.card_view_timestamp);
            mTestDetails =itemView.findViewById (R.id.card_view_notification_details);
        }
    }
}
