package com.example.ebc003.pathologyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ActivityChangePassword extends AppCompatActivity implements View.OnClickListener{

    EditText mEditNewPassword;
    EditText mEditNewPasswordConfirmation;
    Button mBtnResetPassword;
    JSONParser jsonParser=new JSONParser ();
    private String TAG=ActivityChangePassword.class.getSimpleName ();
    String user_emailId;
    String TAG_SUCCESS="success";
    String URI = "http://www.ebusinesscanvas.com/pathalogy_lab/forget_password.php";
    private ProgressDialog pDialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_change_password);

        mEditNewPassword=findViewById (R.id.edit_rest_password);
        mEditNewPasswordConfirmation=findViewById (R.id.edit_rest_confirm_password);
        mBtnResetPassword=findViewById (R.id.btn_rest_password);

        mBtnResetPassword.setOnClickListener (this);

        Intent intent=getIntent ();
        if(Intent.ACTION_VIEW.equals (intent.getAction ())){
            Uri uri=intent.getData ();
            if (uri!=null){
                user_emailId=uri.getQueryParameter ("user_email");
                Log.i (TAG,"URI:-"+user_emailId);
            }
        }
    }

    @Override
    public void onClick (View v) {

        String editNewPassword=mEditNewPassword.getText ().toString ();
        String editNewPasswordConfirmPassword=mEditNewPasswordConfirmation.getText ().toString ();

        if (editNewPassword.equals ("")||editNewPassword.length ()==0){
            mEditNewPassword.requestFocus ();
            mEditNewPassword.setError ("Enter new password");
        }
        else if (editNewPasswordConfirmPassword.equals ("")||editNewPasswordConfirmPassword.length ()==0){
            mEditNewPasswordConfirmation.requestFocus ();
            mEditNewPasswordConfirmation.setError ("Enter new confirm password");
        }
        else if(!editNewPassword.equals (editNewPasswordConfirmPassword)){
            mEditNewPasswordConfirmation.requestFocus ();
            mEditNewPasswordConfirmation.setError ("Password doesn't match");
        }
        else{
            new MyAsyncTask ().execute (editNewPassword,editNewPasswordConfirmPassword);
            Log.i (TAG,"Your password is:-"+editNewPasswordConfirmPassword);
        }
    }

    class MyAsyncTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute () {
            super.onPreExecute ();
            pDialog = new ProgressDialog(ActivityChangePassword.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground (String... strings) {
            String strPassword=strings[0];
            String strPasswordConfirmPassword=strings[1];


            List<NameValuePair> params = new ArrayList<> ();
            params.add(new BasicNameValuePair ("e_mail", user_emailId));
            params.add(new BasicNameValuePair ("password", strPassword));
            params.add(new BasicNameValuePair("confirm_password", strPasswordConfirmPassword));

            Log.i (TAG,"Async Password:-"+strPassword);
            Log.i (TAG,"Async Confirm Password:-"+strPasswordConfirmPassword);
            Log.i (TAG,"Async User EmailID:-"+user_emailId);

            JSONObject json = jsonParser.makeHttpRequest(URI,"POST",params);

            Log.i (TAG,"JASON RESPONSE:-"+json.toString ());

            return null;
        }

        @Override
        protected void onPostExecute (String s) {
            super.onPostExecute (s);
            pDialog.dismiss ();

            Intent intent = new Intent(ActivityChangePassword.this,ResetPasswordSuccess.class);
            startActivity(intent);
            finish();
        }
    }
}
