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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ActivityBookYourVisit extends AppCompatActivity implements Spinner.OnItemSelectedListener  {

    Spinner mSpinnerItems;
    TextView mMiddleName,mLastName,mAge,mContact,mEmailId;
    SharedPreferences preferences;
    String mSharedString;
    private JSONArray mResult;
    ArrayList<String> students;
    Toolbar toolbar;
    String age="";
    String contact="";
    String emailId="";
    String firstName="";
    String middleName="";
    String lastName="";
    LinearLayout bookYourVisitLayout;
    private Snackbar snackbar;
    private boolean internetConnected=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_your_visit);

        bookYourVisitLayout= findViewById(R.id.bookYourVisitLayout);

        mSpinnerItems= findViewById(R.id.spinnerBookVisit);
        mMiddleName= findViewById(R.id.txtMiddleNameBookVisit);
        mLastName= findViewById(R.id.txtLastNameBookVisit);
        mAge= findViewById(R.id.txtAgeBookVisit);
        mContact= findViewById(R.id.txtContactNumberBookVisit);
        mEmailId= findViewById(R.id.txtEMailBookVisit);
        toolbar=findViewById (R.id.book_visit__Toolbar);

        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
             actionBar.setTitle (R.string.book_your_visit);
             actionBar.setDisplayHomeAsUpEnabled (true);
        }

        students=new ArrayList<String>();
        mSpinnerItems.setOnItemSelectedListener(this);

        preferences=getSharedPreferences(Config.SHARED_PREF_NAME,MODE_PRIVATE);
        mSharedString=preferences.getString(Config.EMAIL_SHARED_PREF, "N/A");
        getData(mSharedString);
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
    public BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status=getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    private void setSnackbarMessage(String status, boolean showBar) {
        String internetStatus="";
        if (status.equalsIgnoreCase("wifi enabled")||status.equalsIgnoreCase("mobile data enabled")){
            internetStatus="Internet Connected";
        }
        else{
            internetStatus="Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(bookYourVisitLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
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


    private void getData(String mSharedString) {
        StringRequest stringRequest = new StringRequest("http://www.ebusinesscanvas.com/pathalogy_lab/retrieve_first_name.php?user_email="+mSharedString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            mResult = j.getJSONArray(Config.JSON_ARRAY1);
                            getStudents(mResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getStudents(JSONArray mResult) {
        for(int i=0;i<mResult.length();i++){
            try {
                JSONObject json = mResult.getJSONObject(i);
                students.add(json.getString(Config.TAG_FIRSTNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSpinnerItems.setAdapter(new ArrayAdapter<String>(ActivityBookYourVisit.this,android.R.layout.simple_spinner_dropdown_item,students));
    }

    public String getFirstName(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            firstName=object.getString(Config.TAG_FIRSTNAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstName;
    }

    public String getMiddleName(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            middleName=object.getString(Config.TAG_MIDDLENAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return middleName;
    }

    public String getLastName(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            lastName=object.getString(Config.TAG_LASTNAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lastName;
    }

    public String getAge(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            age=object.getString(Config.TAG_AGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return age;
    }

    public String getContact(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            contact=object.getString(Config.TAG_CONTACTNUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contact;
    }

    public String getEmailId(int position){
        try {
            JSONObject object=mResult.getJSONObject(position);
            emailId=object.getString(Config.TAG_EMAIL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return emailId;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        firstName=getFirstName(position);
        mMiddleName.setText(getMiddleName(position));
        mLastName.setText(getLastName(position));
        mAge.setText(getAge(position));
        mContact.setText(getContact(position));
        mEmailId.setText(getEmailId(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onClickBookVisit(View view) {
        if(mMiddleName.getText().toString().equals("N/A") || mLastName.getText().toString().equals("N/A"))
        {
            Toast.makeText(getApplicationContext(),"Please select patient first name", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), ActivityBookVisitDetails.class);
            Bundle b = new Bundle();
            b.putString("first_name", firstName);
            b.putString("middle_name", middleName);
            b.putString("last_name", lastName);
            b.putString("email_id", emailId);
            b.putString("date_of_birth", age);
            b.putString("contact_number", contact);
            intent.putExtras(b);
            startActivity(intent);
        }
    }
}
