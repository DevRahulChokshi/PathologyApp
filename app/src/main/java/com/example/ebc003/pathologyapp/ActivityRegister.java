package com.example.ebc003.pathologyapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityRegister extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private ProgressDialog pDialog;
    Context context;
    JSONParser jsonParser = new JSONParser();
    NetworkInfo activeNetwork;
    EditText first_name_afm, middle_name_afm, last_name_afm, age_afm, contact_number_afm, e_mail_afm,password,confirm_pwd;
    ImageButton btnDatePicker;
    private int  mYear, mMonth, mDay;
    // url to create new product
    private static String url_create_patient_dtails = "http://www.ebusinesscanvas.com/pathalogy_lab/register.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    //Animation
    private static Animation shakeAnimation;
    Button date;
    String dateString;
    String dateStringButton;
    LinearLayout linearlayout;
    private static final String TAG=ActivityRegister.class.getSimpleName();
    private Snackbar snackbar;
    private boolean internetConnected=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Edit Text
        first_name_afm = findViewById(R.id.first_name_afm_et1);
        middle_name_afm = findViewById(R.id.middle_name_afm_et1);
        last_name_afm = findViewById(R.id.last_name_afm_ett1);
        date = findViewById(R.id.age_afm_et1);
        contact_number_afm = findViewById(R.id.contact_no_afm_et1);
        e_mail_afm = findViewById(R.id.email_afm_et1);
        password = findViewById(R.id.password);
        confirm_pwd= findViewById(R.id.confirm_password);
        btnDatePicker = findViewById(R.id.dobBtn);
        linearlayout= findViewById(R.id.linearlayout);
        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(ActivityRegister.this,
                R.anim.shake);
        //Network
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        Button already_exist= findViewById(R.id.btn_link_login);
        Button btnCreateProduct = findViewById(R.id.submit_afm_btn1);
        date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkValidation();
            }

            private void checkValidation() {
                // Pattern match for email id
                String email = e_mail_afm.getText().toString().trim();
                String first_name=first_name_afm.getText().toString();
                String middle_name=middle_name_afm.getText().toString();
                String last_name=last_name_afm.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+";
                String confirm_pwd1=confirm_pwd.getText().toString();
                String password1=password.getText().toString();
                String dob=date.getText().toString();

                if( first_name.length()==0){
                    first_name_afm.requestFocus();
                    first_name_afm.setError("FIELD CANNOT BE EMPTY");
                     findViewById(R.id.first_name_afm_et1).startAnimation(shakeAnimation);
                }

                else if( middle_name.length()==0){
                    middle_name_afm.requestFocus();
                    middle_name_afm.setError("FIELD CANNOT BE EMPTY");
                    findViewById(R.id.middle_name_afm_et1).startAnimation(shakeAnimation);
                }
                 else if( last_name.length()==0){
                    last_name_afm.requestFocus();
                    last_name_afm.setError("FIELD CANNOT BE EMPTY");
                     findViewById(R.id.last_name_afm_ett1).startAnimation(shakeAnimation);
                }
                else if( dob.length()==0){
                    date.requestFocus();
                    date.setError("FIELD CANNOT BE EMPTY");
                    findViewById(R.id.age_afm_et1).startAnimation(shakeAnimation);
                }
                 else if((contact_number_afm.getText().toString().trim().length() <10)){
                     contact_number_afm.requestFocus();
                     contact_number_afm.setError("ENTER CORRECT CONTACT NO.");
                     findViewById(R.id.contact_no_afm_et1).startAnimation(shakeAnimation);
                 }
                 else if(!email.matches(emailPattern)){
                     e_mail_afm.requestFocus();
                    e_mail_afm.setError("INVALID EMAIL ADDRESS");
                      findViewById(R.id.email_afm_et1).startAnimation(shakeAnimation);
                }
                else if((password.getText().toString().trim().length() <4)){
                     password.requestFocus();
                     password.setError("ENTER ATLEAST 4 CHARACTERS FOR PASSWORD");
                     findViewById(R.id.password).startAnimation(shakeAnimation);
                }
                else if((password.getText().toString().trim().length() >12)){
                    password.requestFocus();
                    password.setError("MAXIMUM PASSWORD LENGHT IS 12");
                    findViewById(R.id.password).startAnimation(shakeAnimation);
                }
                else if(!confirm_pwd1.equals(password1)){
                     confirm_pwd.requestFocus();
                     confirm_pwd.setError("PASSWORD DOES NOT MATCH.");
                     findViewById(R.id.confirm_password).startAnimation(shakeAnimation);
                }
                else{
                    new CreateFamilyDetails().execute();
                }
            }

        });

        already_exist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(ActivityRegister.this,ActivityLogin.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
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
                .make(linearlayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // Log.i(ActivityRegister.class.getSimpleName(),dateStringButton);
        outState.putString("dateString",dateStringButton);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //dateStringButton=savedInstanceState.getString("dateString");
        ActivityRegister.this.date.setText(dateStringButton);
        //Log.i(ActivityRegister.class.getSimpleName(),dateStringButton);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is",""+newVal);
    }
    class CreateFamilyDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityRegister.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String first_name =first_name_afm.getText().toString();
            String middle_name = middle_name_afm.getText().toString();
            String last_name =last_name_afm.getText().toString();
            dateString= ActivityRegister.this.date.getText().toString();
            String contact_number =contact_number_afm.getText().toString();
            String e_mail = e_mail_afm.getText().toString();
            String pass=password.getText().toString();
            String confirm_pass=confirm_pwd.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("first_name", first_name));
            params.add(new BasicNameValuePair("middle_name", middle_name));
            params.add(new BasicNameValuePair("last_name", last_name));
            params.add(new BasicNameValuePair("age", dateString));
            params.add(new BasicNameValuePair("contact_number",contact_number));
            params.add(new BasicNameValuePair("e_mail", e_mail));
            params.add(new BasicNameValuePair("password", pass));
            params.add(new BasicNameValuePair("confirm_password",confirm_pass));

            JSONObject json = jsonParser.makeHttpRequest(url_create_patient_dtails,"POST", params);
            Log.d(ActivityRegister.class.getSimpleName(), json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(ActivityRegister.this, ActivityLogin.class);
                    i.putExtra("e_mail", e_mail + "");
                    startActivity(i);
                    //closing this screen
                    //finish();

                } else {
                    // failed to create product
                    return "false";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            // dismiss the dialog once done
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result == "false")
                Toast.makeText(ActivityRegister.this, "User Name already exists. Please choose another user name ", Toast.LENGTH_LONG).show();
        }
}
    public void show()
    {

        final Dialog d = new Dialog(ActivityRegister.this);
        d.setTitle("Select Birth Date");

        d.setContentView(R.layout.dialog);
        TextView b1 = d.findViewById(R.id.button1);
        TextView b2 = d.findViewById(R.id.button2);
        final NumberPicker np = d.findViewById(R.id.numberPicker1);
        final NumberPicker np1 = d.findViewById(R.id.numberPicker2);
        final NumberPicker np2 = d.findViewById(R.id.numberPicker3);

        Date dateObj=new Date();//Date object
        Calendar calendar=Calendar.getInstance();//Calendar instance
        calendar.setTime(dateObj);

        int dateOfWeek=calendar.get(Calendar.DATE);
        int monthOfWeek=calendar.get(Calendar.MONTH);
        int yearOfWeek=calendar.get(Calendar.YEAR);

        Log.i(TAG,"Today Date:-"+dateOfWeek+" "+monthOfWeek+" "+yearOfWeek);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
        String month=outputFormat.format(cal.getTime());

        Log.i(TAG,"Today Date:-"+month);

        int intMonth=Integer.parseInt(month);

        np.setMaxValue(31); // max value 100
        np.setMinValue(1);  // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        np1.setMaxValue(12); // max value 100
        np1.setMinValue(1);  // min value 0
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(this);

        np2.setMaxValue(yearOfWeek); // max value 100
        np2.setMinValue(1900); // min value 0
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                date.setText(String.valueOf(np.getValue()+"/"+np1.getValue()+"/"+np2.getValue())); //set the value to textview
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}