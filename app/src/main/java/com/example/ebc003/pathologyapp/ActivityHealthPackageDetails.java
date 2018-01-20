package com.example.ebc003.pathologyapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityHealthPackageDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView mTxtComponents,mTxtPrice,mTxtHealthPackName;
    //JSON Array
    private JSONArray mResult;
    ArrayList<String> students1;
    Spinner mSpinnerItems;
    //An ArrayList for Spinner Items
    private ArrayList<String> students;
    SharedPreferences sharedPreferences;
    String email1;
    String health_package,price;
    String package_id;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    static String url_create_patient_details = "http://www.ebusinesscanvas.com/pathalogy_lab/SaveHealthPackage.php";
    SharedPreferences preferences;
    String mSharedString;
    private JSONArray mResult1;
    String id;
    String  patient_id,email,first_name,middle_name,last_name;
    int increment;
    private Snackbar snackbar;
    private boolean internetConnected=true;
    JSONObject json;
    private static final String TAG_SUCCESS = "success";
    CardView CardView;
    String reg_id;
    Toolbar toolbar;

    public String URL="http://www.ebusinesscanvas.com/pathalogy_lab/health_package_history.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_package_deatails);
        mSpinnerItems= findViewById(R.id.spinner1);
        mTxtComponents= findViewById(R.id.textComponents);
        CardView= findViewById(R.id.cardview1);
        mTxtPrice= findViewById(R.id.textPrice);
        mTxtHealthPackName= findViewById(R.id.textHealthName);
        toolbar=findViewById (R.id.health_package_details_toolbar);

        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle ("Health Packages Details");
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        students = new ArrayList<>();
        students1=new ArrayList<>();
        mSpinnerItems.setOnItemSelectedListener(this);
        increment=getIntent().getIntExtra("increment",0);
        ++increment;
        getData(increment);
        preferences=getSharedPreferences(Config.SHARED_PREF_NAME,MODE_PRIVATE);
        mSharedString=preferences.getString(Config.EMAIL_SHARED_PREF, "N/A");
        reg_id=preferences.getString(Config.ID_SHARED_PREF, "N/A");
        getData1(mSharedString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_view_history:
                Intent intent=new Intent (this,MakeAnAppointmentHistory.class);
                intent.putExtra ("card_view_title","Health Package History");
                intent.putExtra ("Email_ID",email);
                intent.putExtra ("URL",URL);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),email);
                startActivity (intent);
                break;

            case android.R.id.home:
                Intent backHealthPackage=new Intent (getApplicationContext (),ActivityHealthPackages.class);
                startActivity (backHealthPackage);
                finish ();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
         sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Fetching the boolean value form sharedpreferences
        email1 = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "N/A");
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
                .make(CardView, internetStatus, Snackbar.LENGTH_INDEFINITE)
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
    private void getData(int id){

        //Creating a string request
        StringRequest stringRequest = new StringRequest("http://www.ebusinesscanvas.com/pathalogy_lab/healthPackageDetails.php?id="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            package_id=Integer.toString (id);
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            mResult = j.getJSONArray(Config.JSON_ARRAY1);
                            //Calling method getStudents to get the students from the JSON Array
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

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void getData1(String mSharedString) {
        StringRequest stringRequest = new StringRequest("http://www.ebusinesscanvas.com/pathalogy_lab/retrieve_first_name.php?user_email="+mSharedString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            mResult1 = j.getJSONArray(Config.JSON_ARRAY1);
                            getStudents1(mResult1);
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
    private void getStudents1(JSONArray mResult) {
        for(int i=0;i<mResult.length();i++){
            try {
                JSONObject json = mResult.getJSONObject(i);
                students1.add(json.getString(Config.TAG_FIRSTNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSpinnerItems.setAdapter(new ArrayAdapter<String>(ActivityHealthPackageDetails.this,android.R.layout.simple_spinner_dropdown_item,students1));
    }

    public String getID(int position){
        try {
            JSONObject object=mResult1.getJSONObject(position);
           patient_id=object.getString("reg_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return patient_id;
    }
    public String getEmail(int position){
        try {
            JSONObject object=mResult1.getJSONObject(position);
            email=object.getString(Config.TAG_EMAIL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }

    public String getFirstName(int position){
        try {
            JSONObject object=mResult1.getJSONObject(position);
            first_name=object.getString(Config.TAG_FIRSTNAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_name;
    }

    public String getMiddleName(int position){
        try {
            JSONObject object=mResult1.getJSONObject(position);
            middle_name=object.getString(Config.TAG_MIDDLENAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return middle_name;
    }

    public String getLastName(int position){
        try {
            JSONObject object=mResult1.getJSONObject(position);
            last_name=object.getString(Config.TAG_LASTNAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return last_name;
    }


    private void getStudents(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                //Adding the name of the student to array list
                students.add(json.getString(Config.TAG_ID));
                students.add(json.getString(Config.TAG_HEALTH));
                students.add(json.getString(Config.TAG_COMP));
                students.add(json.getString(Config.TAG_PRICE));
                display(students);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void display(ArrayList<String> students) {
        health_package=students.get(1);
        String components= students.get(2);
        price= students.get(3);
        id=students.get(0);
        mTxtComponents.setText(components);
        mTxtPrice.setText(price);
        mTxtHealthPackName.setText(health_package);
    }

    public void onSelectHealthPackage(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityHealthPackageDetails.this);
            dialog.setCancelable(false);
            dialog.setTitle("Cost of the test");
            dialog.setMessage("Do you really want to select this package?" );
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                     PostHealthPackage healthPackage=new PostHealthPackage();
                    healthPackage.execute(email1,health_package);
                }
            })
                    .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                        }
                    });
            final AlertDialog alert = dialog.create();
            alert.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        patient_id = getID(position);
        email=getEmail(position);
        first_name=getFirstName (position);
        middle_name=getMiddleName (position);
        last_name=getLastName (position);

        Log.i (ActivityHealthPackageDetails.class.getSimpleName (),"Spinner Email:-"+email);
        Log.i (ActivityHealthPackageDetails.class.getSimpleName (),"Spinner Patient ID:-"+patient_id);
        Log.i (ActivityHealthPackageDetails.class.getSimpleName (),"Spinner Patient first name:-"+first_name);
        Log.i (ActivityHealthPackageDetails.class.getSimpleName (),"Spinner Patient middle name:-"+middle_name);
        Log.i (ActivityHealthPackageDetails.class.getSimpleName (),"Spinner Patient last name:-"+last_name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class PostHealthPackage extends AsyncTask<String, String, String> {

        String mDate=getDateTime();
        String mPatientName=first_name+" "+middle_name+" "+last_name;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ActivityHealthPackageDetails.this);
            pDialog.setMessage("Posting data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                List<NameValuePair> listdata = new ArrayList<NameValuePair>();
                listdata.add(new BasicNameValuePair("user_email", email));
                listdata.add(new BasicNameValuePair("package_id",package_id));
                listdata.add(new BasicNameValuePair("patient_id", patient_id));
                listdata.add(new BasicNameValuePair("reg_id", reg_id));
                listdata.add(new BasicNameValuePair("date_time", mDate));
                listdata.add(new BasicNameValuePair("patient_name", mPatientName));
                listdata.add(new BasicNameValuePair("package_name", health_package));
                listdata.add(new BasicNameValuePair("package_price", price));

                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),email);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),package_id);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),patient_id);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),reg_id);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),mDate);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),mPatientName);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),health_package);
                Log.i (ActivityHealthPackageDetails.class.getSimpleName (),price);

                json = jsonParser.makeHttpRequest(url_create_patient_details, "POST", listdata);
                Log.d(ActivityHealthPackageDetails.class.getSimpleName(), json.toString());
                listdata.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created product
                    return "true";

                } else if (success == 0) {
                    return "false";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result  ) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (result.equals ("true")) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityHealthPackageDetails.this);
                dialog.setCancelable(false);

                dialog.setMessage("Are you sure to select this package?");
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ActivityHealthPackageDetails.this,ActivityDashboard.class);
                        startActivity(intent);
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();

            }
            if (result.equals ("false")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityHealthPackageDetails.this);
                dialog.setCancelable(false);

                dialog.setMessage("You can select only one package");
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();

            }
        }
    }

    private String getDateTime () {

        Date date=new Date ();
        Calendar calendar=Calendar.getInstance ();
        calendar.setTime (date);
        int cYear=calendar.get (Calendar.YEAR);
        int cMonth=(calendar.get (Calendar.MONTH)+1);
        int cDate=calendar.get (Calendar.DATE);

        int cHour=calendar.get (Calendar.HOUR);
        int cMinute=calendar.get (Calendar.MINUTE);
        int cSecond=calendar.get (Calendar.SECOND);

        return (cYear+"-"+cMonth+"-"+cDate+" "+cHour+":"+cMinute+":"+cSecond);
    }
}
