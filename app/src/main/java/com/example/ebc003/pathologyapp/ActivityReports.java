package com.example.ebc003.pathologyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ActivityReports extends AppCompatActivity  {

    private static final int STORAGE_PERMISSION_CODE = 123;
    NetworkInfo activeNetwork;
    ListView listView;
    ArrayList<Pdf> pdfList = new ArrayList<Pdf>();
    PdfAdapter pdfAdapter;
    SharedPreferences sharedPreferences;
    String email1;
    LinearLayout reportsLayout;
    private Snackbar snackbar;
    Toolbar toolbar;

    private boolean internetConnected=true;
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
        setContentView(R.layout.activity_reports);

        toolbar=findViewById (R.id.report_Toolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (R.string.report);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Fetching the boolean value form sharedpreferences
        email1 = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "N/A");
        reportsLayout= findViewById(R.id.reportsLayout);
        //initializing ListView
        listView = findViewById(R.id.listView);
        listView.setDivider(new ColorDrawable(R.color.dark_blue));   //0xAARRGGBB
        listView.setDividerHeight(1);

         if(activeNetwork==null)
        {
            Toast.makeText(getApplicationContext(),  "No internet Connectivity", Toast.LENGTH_LONG).show();
        }
        else
      {
          getPdfs(email1);
      }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pdf pdf = (Pdf) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(pdf.getUrl()));
                startActivity(intent);
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
                .make(reportsLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
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

    private void home() {
        Intent intent = new Intent(ActivityReports.this,ActivityDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void getPdfs(String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.ebusinesscanvas.com/pathalogy_lab/reports.php?user_email="+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pdfList.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("pdfs");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //Declaring a json object corresponding to every pdf object in our json Array
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //Declaring a Pdf object to add it to the ArrayList  pdfList
                                Pdf pdf = new Pdf();
                                String pdfName = jsonObject.getString("report_name");
                                String pdfUrl = jsonObject.getString("url");
                                String pdfFamily=jsonObject.getString("family_member_name");
                                pdf.setName(pdfName);
                                pdf.setUrl(pdfUrl);
                                pdf.setFamily(pdfFamily);
                                pdfList.add(pdf);}
                            pdfAdapter = new PdfAdapter(ActivityReports.this, R.layout.list_layout1, pdfList);
                            listView.setAdapter(pdfAdapter);
                            pdfAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);}

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}