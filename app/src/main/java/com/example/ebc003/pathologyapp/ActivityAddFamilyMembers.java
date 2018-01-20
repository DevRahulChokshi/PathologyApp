package com.example.ebc003.pathologyapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.afollestad.materialdialogs.MaterialDialog;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityAddFamilyMembers extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private static final String TAG = ActivityAddFamilyMembers.class.getSimpleName();
    private ProgressDialog pDialog;
    //defining AwesomeValidation object
    String emailID;
    String register_ID;
    JSONParser jsonParser = new JSONParser();
    final Context context = this;
    EditText first_name_afm, middle_name_afm, last_name_afm, contact_number_afm, e_mail_afm;
    NetworkInfo activeNetwork;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    ImageButton btnDatePicker;
    private static Animation shakeAnimation;
    Button date;
    LinearLayout addFamilyLayout;
    private Snackbar snackbar;
    private boolean internetConnected=true;
    Button mBtnMemberRelation;
    private Toast toast;
    private Thread thread;
    ArrayList<String>mArrayFamilyMemberRelation;
    String mStrFamilyRelation;
    Toolbar toolbar;


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void startThread(Runnable run) {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(run);
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_members);
        shakeAnimation = AnimationUtils.loadAnimation(ActivityAddFamilyMembers.this,
                R.anim.shake);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();

        date = findViewById(R.id.age_afm_et);
        addFamilyLayout= findViewById(R.id.addFamilyLayout);
        // Edit Text
        first_name_afm = findViewById(R.id.first_name_afm_et);
        middle_name_afm = findViewById(R.id.middle_name_afm_et);
        last_name_afm = findViewById(R.id.last_name_afm_ett);
        contact_number_afm = findViewById(R.id.contact_no_afm_et);
        e_mail_afm = findViewById(R.id.email_afm_et);
        btnDatePicker = findViewById(R.id.photoButton1);
        mBtnMemberRelation =findViewById(R.id.btn_member_relation);
        date.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                show();
            }
        });

        mArrayFamilyMemberRelation=new ArrayList<>();
        mArrayFamilyMemberRelation.add("Grand-Father");
        mArrayFamilyMemberRelation.add("Grand-Mother");
        mArrayFamilyMemberRelation.add("Father");
        mArrayFamilyMemberRelation.add("Mother");
        mArrayFamilyMemberRelation.add("Brother");
        mArrayFamilyMemberRelation.add("Sister");
        mArrayFamilyMemberRelation.add("Other");

        mBtnMemberRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoice();
            }
        });

        toolbar=findViewById (R.id.add_your_family_Toolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (R.string.add_family_members);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
    }

    //On ButtonMemberRelation show dialog with multiple option
    public void showMultiChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.family_relation)
                .items(mArrayFamilyMemberRelation)
                .itemsCallbackSingleChoice(
                        2,
                        (dialog, view, which, text) -> {
                            mStrFamilyRelation=text.toString();
                            mBtnMemberRelation.setText(mStrFamilyRelation);
                            return true; // allow selection
                        })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    //OnClick AddFamilyMember button check validation
    public void OnClickAddFamilyMember(View view) {
        checkValidation();
    }

    private void checkValidation() {
        String email = e_mail_afm.getText().toString().trim();
        String first_name=first_name_afm.getText().toString();
        String middle_name=middle_name_afm.getText().toString();
        String last_name=last_name_afm.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+";

        if( first_name.length()==0){
            first_name_afm.requestFocus();
            first_name_afm.setError("PLEASE ENTER FIRST NAME");
            findViewById(R.id.first_name_afm_et).startAnimation(shakeAnimation);
        }

        else if( last_name.length()==0){
            last_name_afm.requestFocus();
            last_name_afm.setError("PLEASE ENTER LAST NAME");
            findViewById(R.id.last_name_afm_ett).startAnimation(shakeAnimation);
        }

        else if( date.getText().toString().length()==0){
            date.requestFocus();
            date.setError("PLEASE ENTER AGE");
            findViewById(R.id.age_afm_et).startAnimation(shakeAnimation);
        }
        else if((contact_number_afm.getText().toString().trim().length() <10)){
            contact_number_afm.requestFocus();
            contact_number_afm.setError("ENTER APPROPRIATE CONTACT NO.");
            findViewById(R.id.contact_no_afm_et).startAnimation(shakeAnimation);
        }
        else if(email.equals(emailID))
        {
            Toast.makeText(getApplicationContext(),"USER EMAIL ALREADY EXISTS", Toast.LENGTH_LONG).show();
        }
        else if(mStrFamilyRelation==null)
        {
            mBtnMemberRelation.requestFocus();
            mBtnMemberRelation.setError("PLEASE CHOOSE FAMILY RELATION");
            findViewById(R.id.btn_member_relation).startAnimation(shakeAnimation);
        }

        else if(!email.equals("")){
            if (!email.matches(emailPattern))
                Toast.makeText(getApplicationContext(),"INVALID EMAIL ADDRESS",Toast.LENGTH_SHORT).show();

            else {
                new CreateFamilyDetails().execute();
            }
        }
        else{
            new CreateFamilyDetails().execute();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //In on resume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Fetching the boolean value form sharedpreferences
        emailID = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "");
        register_ID = sharedPreferences.getString(Config.ID_SHARED_PREF, "");
        Log.i(TAG,register_ID);
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
                .make(addFamilyLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
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
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is",""+newVal);
    }

    class CreateFamilyDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityAddFamilyMembers.this);
            pDialog.setMessage("Insert Data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {
            String first_name =first_name_afm.getText().toString();
            String middle_name = middle_name_afm.getText().toString();
            String last_name =last_name_afm.getText().toString();
            String age =date.getText().toString();
            String contact_number =contact_number_afm.getText().toString();
            String e_mail = e_mail_afm.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first_name", first_name));
            params.add(new BasicNameValuePair("middle_name", middle_name));
            params.add(new BasicNameValuePair("last_name", last_name));
            params.add(new BasicNameValuePair("age", age));
            params.add(new BasicNameValuePair("contact_number", contact_number));
            params.add(new BasicNameValuePair("e_mail", e_mail));
            params.add(new BasicNameValuePair("user_email",emailID));
            params.add(new BasicNameValuePair("family_relation",mStrFamilyRelation));
            params.add(new BasicNameValuePair(Config.ID_SHARED_PREF,register_ID));
            Log.i(TAG,mStrFamilyRelation);
            Log.i(TAG,register_ID);
            String url_create_patient_dtails = "http://www.ebusinesscanvas.com/pathalogy_lab/add_family_member_details.php";
            JSONObject json = jsonParser.makeHttpRequest(url_create_patient_dtails,"POST", params);
            Log.d(ActivityAddFamilyMembers.class.getSimpleName(), json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(ActivityAddFamilyMembers.this, ActivityDashboard.class);
                    i.putExtra("e_mail", e_mail + "");
                    startActivity(i);
                } else {
                    return "false";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            pDialog.dismiss();
            if (result == "false")
                Toast.makeText(ActivityAddFamilyMembers.this, "User Name already exists. Please choose another user name ", Toast.LENGTH_LONG).show();
        }
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void show()
    {
        final Dialog d = new Dialog(ActivityAddFamilyMembers.this);
        d.setTitle("Select Birth Date");

        d.setContentView(R.layout.dialog);
        Button b1 = d.findViewById(R.id.button1);
        Button b2 = d.findViewById(R.id.button2);
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


        np.setMaxValue(31);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        np1.setMaxValue(12);
        np1.setMinValue(1);
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(this);

        np2.setMaxValue(yearOfWeek);
        np2.setMinValue(1900);
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
}