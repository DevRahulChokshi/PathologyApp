package com.example.ebc003.pathologyapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityContactUs extends AppCompatActivity {

    NetworkInfo activeNetwork;
    List<Subjects1> subjectsList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    String HTTP_JSON_URL = "http://www.ebusinesscanvas.com/pathalogy_lab/contact_us.php";
    String Path_lab_name= "pathology_lab_name";
    String GET_JSON_FROM_SERVER_NAME = "email";
    String JSON_ID = "website";
    String JSON_Desc = "contact_no";
    String JSON_day = "opening_day";
    String JSON_day1 = "closing_day";
    String JSON_time = "opening_time";
    String JSON_time1 = "closing_time";
    String address="address";
    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;
    View ChildView ;
    int GetItemPosition ;
    ArrayList<String> SubjectNames;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        subjectsList = new ArrayList<>();

        toolbar=findViewById (R.id.contact_us_Toolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (R.string.contact_us);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        SubjectNames = new ArrayList<>();

        if(activeNetwork==null) {
            Toast.makeText(getApplicationContext(),  "No internet Connectivity", Toast.LENGTH_LONG).show();
        }
        else {
        JSON_DATA_WEB_CALL();
        }

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(ActivityContactUs.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    GetItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }


    public void JSON_DATA_WEB_CALL(){
        jsonArrayRequest = new JsonArrayRequest(HTTP_JSON_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            Subjects1 GetDataAdapter2 = new Subjects1();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                GetDataAdapter2.setPathName(json.getString(Path_lab_name));
                GetDataAdapter2.setName(json.getString(GET_JSON_FROM_SERVER_NAME));
                //SubjectNames.add(json.getString(GET_JSON_FROM_SERVER_NAME));
                GetDataAdapter2.setId(json.getString(JSON_ID));
                //SubjectNames.add(json.getString(JSON_ID));
                GetDataAdapter2.setDesc(json.getString(JSON_Desc));
                //SubjectNames.add(json.getString(JSON_Desc));
                GetDataAdapter2.setDay(json.getString(JSON_day)+"-"+json.getString(JSON_day1));
                GetDataAdapter2.setTime(json.getString(JSON_time)+"-"+json.getString(JSON_time1));
                GetDataAdapter2.setAddress(json.getString(address));
            } catch (JSONException e) {

                e.printStackTrace();
            }
            subjectsList.add(GetDataAdapter2);
        }

        recyclerViewadapter = new RecyclerViewCardViewAdapter1(subjectsList, this);

        recyclerView.setAdapter(recyclerViewadapter);

    }



    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}