package com.example.ebc003.pathologyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ActivityHealthPackages extends AppCompatActivity {
    NetworkInfo activeNetwork;
    List<Subjects> subjectsList;
    // recyclerView
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    RecyclerView.Adapter recyclerViewAdapter;
    String HTTP_JSON_URL = "http://www.ebusinesscanvas.com/pathalogy_lab/health_pkgs.php";
    String GET_JSON_FROM_SERVER_NAME = "name_of_health_pack";
    String JSON_ID = "price";
    String JSON_Desc = "components";
    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;
    View ChildView ;
    int GetItemPosition ;
    ArrayList<String> SubjectNames;
    private Snackbar snackbar;
    private boolean internetConnected=true;
    Toolbar toolbar;
    LinearLayout linearLayout;
    SharedPreferences preferences;

    public BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status=getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_health_package);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        subjectsList = new ArrayList<>();

        linearLayout=findViewById (R.id.health_package);
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        SubjectNames = new ArrayList<>();

        toolbar=findViewById (R.id.health_package_toolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (R.string.health_packages);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        JSON_DATA_WEB_CALL();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(ActivityHealthPackages.this, new GestureDetector.SimpleOnGestureListener() {

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

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    private void registerInternetCheckReceiver() {
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver,intentFilter);
    }

    private void setSnackbarMessage(String status, boolean showBar) {
        String internetStatus="";
        if (status.equalsIgnoreCase("wifi enabled")||status.equalsIgnoreCase("mobile data enabled")){
            internetStatus="Internet Connected";
        }
        else{
            internetStatus="Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(linearLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        snackbar.setActionTextColor(Color.GREEN);

        View sbView=snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        if(internetStatus.equalsIgnoreCase("Lost Internet Connection")){
            if(internetConnected){
                snackbar.show();
                internetConnected=false;
            }
        }else{
            if(!internetConnected){
                internetConnected=true;
                snackbar.show();
            }
        }
    }
    private String getConnectivityStatusString(Context context) {
        int conn=getConnectivityStatus(context);
        String status=null;

        if(conn==Config.TYPE_WIFI){
            status="wifi enabled";
        }
        else if(conn==Config.TYPE_MOBILE){
            status="mobile data enabled";
        }
        else if(conn==Config.TYPE_NOT_CONNECTED){
            status="internet not connected";
        }
        return status;
    }
    private int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

        if(null!=networkInfo){
            if (networkInfo.getType()== ConnectivityManager.TYPE_WIFI){
                return Config.TYPE_WIFI;
            }

            if (networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                return Config.TYPE_MOBILE;
            }
        }
        return Config.TYPE_NOT_CONNECTED;
    }

    public void JSON_DATA_WEB_CALL(){
        jsonArrayRequest = new JsonArrayRequest(HTTP_JSON_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSON_PARSE_DATA_AFTER_WEB_CALL (response);
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

    public void JSON_PARSE_DATA_AFTER_WEB_CALL (JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            Subjects GetDataAdapter2 = new Subjects();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setName(json.getString(GET_JSON_FROM_SERVER_NAME));
                GetDataAdapter2.setId(json.getString(JSON_ID));
                } catch (JSONException e) {
                e.printStackTrace();
            }
            subjectsList.add(GetDataAdapter2);
        }
        recyclerViewAdapter = new RecyclerViewCardViewAdapter(subjectsList, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}