package com.example.ebc003.pathologyapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by admin-2 on 5/18/2017.
 */
public class RecyclerViewCardViewAdapter extends RecyclerView.Adapter<RecyclerViewCardViewAdapter.ViewHolder> {

    Context context;

    List<Subjects> Subjects;
    String sub_name="";

    int increment;
    public RecyclerViewCardViewAdapter(List<Subjects> getDataAdapter, Context context){
        super();
        this.Subjects = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subjects getDataAdapter1 =  Subjects.get(position);
        holder.SubjectName.setText(getDataAdapter1.getName());
        holder.IdTextView.setText(getDataAdapter1.getId());
      //  holder.DescTextView.setText(getDataAdapter1.getDesc());

    }

    @Override
    public int getItemCount() {
        return Subjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView SubjectName;
        public TextView IdTextView;
       // public TextView DescTextView;

        public ViewHolder(View itemView) {

            super(itemView);
            SubjectName = itemView.findViewById(R.id.textView2);
            IdTextView = itemView.findViewById(R.id.textView4);
            //DescTextView = (TextView) itemView.findViewById(R.id.textView6) ;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,ActivityHealthPackageDetails.class);
                    increment=getAdapterPosition();

                    intent.putExtra("increment",increment);
                    context.startActivity(intent);
                }
            });
        }
    }
}
