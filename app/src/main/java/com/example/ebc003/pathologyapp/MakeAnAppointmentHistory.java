package com.example.ebc003.pathologyapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeAnAppointmentHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LinearLayoutManager mLinearLayoutManagerVertical;
    String TAG=MakeAnAppointmentHistory.class.getSimpleName ();
    List<AppointmentData> appointmentDataList;
    String Email_ID;
    String URL;
    String cardViewTitle;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_make_an_appointment_history);

        recyclerView=findViewById (R.id.recyclerViewAppointment);

        recyclerView.setHasFixedSize(true);
        mLinearLayoutManagerVertical = new LinearLayoutManager(this);

        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        appointmentDataList =new ArrayList<> ();

        Bundle bundle=getIntent ().getExtras();
        if (bundle!=null){
            cardViewTitle=bundle.getString ("card_view_title","N/A");
            Email_ID=bundle.getString ("Email_ID","N/A");
            URL=bundle.getString ("URL","N/A");
        }else {
            Toast.makeText (getApplicationContext (),"Bundle EMPTY",Toast.LENGTH_LONG).show ();
        }

        if (Email_ID!=null){
            Log.i (TAG,Email_ID);
        }else {
            Toast.makeText (getApplicationContext (),"Email ID EMPTY",Toast.LENGTH_LONG).show ();
        }

        Toolbar toolbar=new Toolbar (this);
        toolbar=findViewById (R.id.make_appointment_history_Toolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (cardViewTitle);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        getData ();
    }

    private void getData(){

        //Creating a string request
        StringRequest stringRequest = new StringRequest (Request.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONArray(response);
                            int size=j.length ();
                            Log.i (TAG,"Size:-"+size);
                            for (int i=0;i<=j.length ();i++){
                                try{
                                    if (URL.equals (Config.URL))
                                    {
                                        AppointmentData appointmentData=new AppointmentData ();

                                        String userName=j.getJSONObject (i).getString ("patient_name");
                                        String userTestName=j.getJSONObject (i).getString ("package_name");
                                        String userScheduleName=j.getJSONObject (i).getString ("package_price");
                                        String userTime=j.getJSONObject (i).getString ("date_time");

                                        appointmentData.setUserName (userName);
                                        appointmentData.setTest_type_Name (userTestName);
                                        appointmentData.setSchedule (userScheduleName);
                                        appointmentData.setTime (userTime);

                                        appointmentDataList.add (appointmentData);
                                    }
                                    else {
                                        AppointmentData appointmentData=new AppointmentData ();

                                        String userName=j.getJSONObject (i).getString ("patient_full_name");
                                        String userTestName=j.getJSONObject (i).getString ("test_name");
                                        String userScheduleName=j.getJSONObject (i).getString ("schedule_date");
                                        String userTime=j.getJSONObject (i).getString ("schedule_time");
                                        appointmentData.setUserName (userName);
                                        appointmentData.setTest_type_Name (userTestName);
                                        appointmentData.setSchedule (userScheduleName);
                                        appointmentData.setTime (userTime);

                                        appointmentDataList.add (appointmentData);
                                    }

                                }catch (JSONException e){
                                    e.printStackTrace ();
                                }
                            }
                            setRecyclerView ();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams () throws AuthFailureError {
                Log.i (TAG,"MAP EMAIL_ID:-"+Email_ID);
                Map<String,String> stringMap=new HashMap<> ();
                stringMap.put ("e_mail",Email_ID);
                return stringMap;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void setRecyclerView () {
        recyclerViewAdapter=new RecyclerViewAdapter (this,appointmentDataList);
        recyclerView.setAdapter (recyclerViewAdapter);
    }
}
