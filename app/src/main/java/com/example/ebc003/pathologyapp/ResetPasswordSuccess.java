package com.example.ebc003.pathologyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ResetPasswordSuccess extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_reset_password_success);

        Button loginSuccess=findViewById (R.id.login_success);

        loginSuccess.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
              Intent intent=new Intent (getApplicationContext (),ActivityLogin.class);
              startActivity (intent);
            }
        });
    }
}
