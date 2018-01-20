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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class ActivityForgetPassword extends AppCompatActivity {
    EditText email;

    Button forgot_email;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    // url to create new product
    private static String url_create_patient_details = "http://www.ebusinesscanvas.com/pathalogy_lab/reset_pwd.php";
    LinearLayout forgetPasswordLayout;
    private Snackbar snackbar;
    private boolean internetConnected=true;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email= findViewById(R.id.forget_email);
        forgot_email = findViewById(R.id.reset_password_btn);
        forgetPasswordLayout= findViewById(R.id.forgetPasswordLayout);

        forgot_email.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         validation();}
        }
        );
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
                .make(forgetPasswordLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
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

    private void validation() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+";
       if(!email.getText().toString().matches(emailPattern)){
            email.requestFocus();
            email.setError("INVALID EMAIL ADDRESS");

        }
        else
           new CreateFamilyDetails().execute();
    }

  public  class CreateFamilyDetails extends AsyncTask<String, String, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityForgetPassword.this);
            pDialog.setMessage("Verifying..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String email_id = email.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("e_mail", email_id));
            JSONObject json = jsonParser.makeHttpRequest(url_create_patient_details,"POST", params);
            Log.d(ActivityForgetPassword.class.getSimpleName(), json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created product
                    return "true";
                } else if (success == 0){
                    return "false";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss();

            if(result=="true") {
//                SharedPreferences sharedPreferences = ActivityForgetPassword.this.getSharedPreferences (Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit ();
//                editor.putBoolean (Config.LOGGEDIN_SHARED_PREF, true);
//                editor.putString (Config.EMAIL_SHARED_PREF, email.getText ().toString ());
//                editor.apply ();

                Intent intent=new Intent (ActivityForgetPassword.this,ActivityLogin.class);
                intent.putExtra ("user_name",email.getText ().toString ());
                startActivity (intent);

                Toast.makeText (getApplicationContext (),"Please check your EMAIL",Toast.LENGTH_LONG).show ();
            }
            else if (result == "false")
                Toast.makeText(getApplicationContext(),"Enter Registered Email ID",Toast.LENGTH_LONG).show();
            }
    }
}
