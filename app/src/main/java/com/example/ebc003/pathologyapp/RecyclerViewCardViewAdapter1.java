package com.example.ebc003.pathologyapp;

/**
 * Created by Juned on 1/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewCardViewAdapter1 extends RecyclerView.Adapter<RecyclerViewCardViewAdapter1.ViewHolder> {

    Context context;

    List<Subjects1> Subjects1;

    public RecyclerViewCardViewAdapter1(List<Subjects1> getDataAdapter, Context context){

        super();

        this.Subjects1 = getDataAdapter;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contact, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Subjects1 getDataAdapter1 =  Subjects1.get(position);
        holder.path_lab_name.setText(getDataAdapter1.getPathName());
        holder.SubjectName.setText(getDataAdapter1.getName());
        holder.IdTextView.setText(getDataAdapter1.getId());
        holder.DescTextView.setText(getDataAdapter1.getDesc());
        holder.DescDay.setText(getDataAdapter1.getDay());
        holder.DescTime.setText(getDataAdapter1.getTime());
        holder.address.setText(getDataAdapter1.getAddress());

    }

    @Override
    public int getItemCount() {

        return Subjects1.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView SubjectName;
        public TextView IdTextView;
        public TextView DescTextView;
        public TextView DescDay;
        public TextView DescTime,address;
        public TextView path_lab_name;

        public ViewHolder(View itemView) {

            super(itemView);
            path_lab_name= itemView.findViewById(R.id.path_lab_name);
            path_lab_name.setMovementMethod(LinkMovementMethod.getInstance());
            SubjectName = itemView.findViewById(R.id.textView4);
            SubjectName.setMovementMethod(LinkMovementMethod.getInstance());
            IdTextView = itemView.findViewById(R.id.textView6);
            IdTextView.setMovementMethod(LinkMovementMethod.getInstance());
            DescTextView = itemView.findViewById(R.id.textView8);
            DescTextView.setMovementMethod(LinkMovementMethod.getInstance());
            DescDay = itemView.findViewById(R.id.opening_day);

            DescTime = itemView.findViewById(R.id.opening_time);
            address = itemView.findViewById(R.id.address);

        }
    }
}