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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private TextView forgetpwd;
    LinearLayout linearLayout;
    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin, buttonSignup;
    private static Animation shakeAnimation;
    private String email;
    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    Context context;
    NetworkInfo activeNetwork;
    private Snackbar snackbar;
    private boolean internetConnected = true;
    String TAG=ActivityLogin.class.getSimpleName ();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext ()
                .getSystemService (Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo ();
        //Initializing views
        forgetpwd = findViewById (R.id.mforgetpwd);
        editTextEmail = findViewById (R.id.editTextEmail);
        editTextPassword = findViewById (R.id.editTextPassword);
        email = editTextEmail.getText ().toString ().trim ();
        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation (ActivityLogin.this,
                R.anim.shake);

        linearLayout = findViewById (R.id.loginLayout);
        buttonLogin = findViewById (R.id.buttonLogin);
        buttonSignup = findViewById (R.id.linkSignup);
        //Adding click listener
        buttonLogin.setOnClickListener (this);
        buttonSignup.setOnClickListener (this);
        buttonSignup.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (ActivityLogin.this, ActivityRegister.class);
                startActivity (i);
            }
        });
    }

    @Override
    protected void onResume () {
        super.onResume ();
        registerInternetCheckReceiver ();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences (Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean (Config.LOGGEDIN_SHARED_PREF, false);
        //If we will get true
        if (loggedIn) {
            //We will start the Profile Activity
            Intent intent = new Intent (ActivityLogin.this, ActivityDashboard.class);
            startActivity (intent);
        }
    }

    private void login () {
        //Getting values from edit texts
        final String email = editTextEmail.getText ().toString ().trim ();
        final String password = editTextPassword.getText ().toString ().trim ();
        //Creating a string request
        StringRequest stringRequest = new StringRequest (Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse (String response) {
                        Log.i (TAG,response);
                        JSONObject j = null;
                        try {
                            j=new JSONObject (response);
                            String json=j.getString ("status");
                            Log.i (TAG,""+json);
                            if (json.equals (Config.LOGIN_SUCCESS)){
                                String userRegId=j.getString ("register_id");
                                SharedPreferences sharedPreferences = ActivityLogin.this.getSharedPreferences (Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit ();
                                //Adding values to editor
                                editor.putBoolean (Config.LOGGEDIN_SHARED_PREF, true);
                                editor.putString (Config.EMAIL_SHARED_PREF, email);
                                editor.putString (Config.ID_SHARED_PREF, userRegId);
                                //Saving values to editor
                                editor.commit ();
                                //Starting profile activity
                                Intent intent = new Intent (ActivityLogin.this, ActivityDashboard.class);
                                startActivity (intent);
                            }else{
                                Toast.makeText (ActivityLogin.this, "Invalid username or password", Toast.LENGTH_LONG).show ();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse (VolleyError error) {
                        //You can handle error here if you want
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams () throws AuthFailureError {
                        Map<String, String> params = new HashMap<> ();
                        //Adding parameters to request
                        params.put (Config.KEY_EMAIL, email);
                        params.put (Config.KEY_PASSWORD, password);

                        //returning parameter
                        return params;
                    }
                };
                //Adding the string request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue (this);
                requestQueue.add (stringRequest);
    }

    private void registerInternetCheckReceiver () {
        IntentFilter intentFilter = new IntentFilter ();
        intentFilter.addAction ("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction ("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver (broadcastReceiver, intentFilter);
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context, Intent intent) {
            String status = getConnectivityStatusString (context);
            setSnackbarMessage (status, false);
        }
    };

    private void setSnackbarMessage (String status, boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase ("wifi enabled") || status.equalsIgnoreCase ("mobile data enabled")) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "Lost Internet Connection";
        }
        snackbar = Snackbar
                .make (linearLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
                .setAction ("Dismiss", new View.OnClickListener () {
                    @Override
                    public void onClick (View view) {
                        snackbar.dismiss ();
                    }
                });
        snackbar.setActionTextColor (Color.GREEN);

        View sbView = snackbar.getView ();
        TextView textView = sbView.findViewById (android.support.design.R.id.snackbar_text);
        textView.setTextColor (Color.WHITE);

        if (internetStatus.equalsIgnoreCase ("Lost Internet Connection")) {
            if (internetConnected) {
                snackbar.show ();
                internetConnected = false;
            }
        } else {
            if (! internetConnected) {
                internetConnected = true;
                snackbar.show ();
            }
        }
    }

    private String getConnectivityStatusString (Context context) {
        int conn = getConnectivityStatus (context);
        String status = null;

        if (conn == Config.TYPE_WIFI) {
            status = "wifi enabled";
        } else if (conn == Config.TYPE_MOBILE) {
            status = "mobile data enabled";
        } else if (conn == Config.TYPE_NOT_CONNECTED) {
            status = "internet not connected";
        }
        return status;
    }

    private int getConnectivityStatus (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService (CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo ();
        if (null != networkInfo) {
            if (networkInfo.getType () == ConnectivityManager.TYPE_WIFI) {
                return Config.TYPE_WIFI;
            }
            if (networkInfo.getType () == ConnectivityManager.TYPE_MOBILE) {
                return Config.TYPE_MOBILE;
            }
        }
        return Config.TYPE_NOT_CONNECTED;
    }

    @Override
    public void onClick (View v) {
        //Calling the login function
        checkValidation ();
    }

    // Check Validation before login
    private void checkValidation () {
        // Get email id and password
        String getEmailId = editTextEmail.getText ().toString ();
        String getPassword = editTextPassword.getText ().toString ();
        String email = editTextEmail.getText ().toString ().trim ();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+";
        // Check patter for email id
        Pattern p = Pattern.compile (Utils.regEx);
        Matcher m = p.matcher (getEmailId);
        // Check for both field is empty or not
        if (getEmailId.equals ("") || getEmailId.length () == 0) {
            editTextEmail.requestFocus ();
            editTextEmail.setError ("FIELD CANNOT BE EMPTY");
            findViewById (R.id.editTextEmail).startAnimation (shakeAnimation);
        } else if (getPassword.equals ("") || getPassword.length () == 0) {
            editTextPassword.requestFocus ();
            editTextPassword.setError ("FIELD CANNOT BE EMPTY");
            findViewById (R.id.buttonLogin).startAnimation (shakeAnimation);
        } else if (! email.matches (emailPattern)) {
            Toast.makeText (getApplicationContext (), "invalid email address", Toast.LENGTH_SHORT).show ();
        } else {
            login ();
        }
    }

    public void onRestPasswordActivity (View view) {
        Intent intent = new Intent (this, ActivityForgetPassword.class);
        startActivity (intent);
    }
}
