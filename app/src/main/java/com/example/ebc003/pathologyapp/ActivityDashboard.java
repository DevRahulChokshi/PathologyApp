package com.example.ebc003.pathologyapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class ActivityDashboard extends AppCompatActivity {

    // Alert Dialog Manager
    private FloatingActionButton fab;
    SharedPreferences preferences;
    TextView t1;
    String email;
    String id;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    int k=0;
    TextView textCartItemCount;
    int mCartItemCount = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar myToolBar=findViewById(R.id.dashboardToolbar);
        setSupportActionBar(myToolBar);
        ActionBar actionBar=getSupportActionBar ();
        if (actionBar!=null){
            actionBar.setTitle (R.string.toolbar_title);
        }

        gridView = findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this,R.layout.grid_item_layout,getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position==0) {
                    Intent intent=new Intent(getApplicationContext(),ActivityMakeAppointment.class);
                    startActivity(intent);
                }
                else if (position==1) {
                    Intent intent=new Intent(getApplicationContext(),ActivityBookYourVisit.class);
                    startActivity(intent);
                }
                else if (position==2) {
                    Intent intent=new Intent(getApplicationContext(),ActivityHealthPackages.class);
                    startActivity(intent);
                }
                else if (position==3) {
                    Intent intent=new Intent(getApplicationContext(),ActivityAddFamilyMembers.class);
                    startActivity(intent);
                }
                else if (position==4) {
                    Intent intent=new Intent(getApplicationContext(),ActivityReports.class);
                    startActivity(intent);
                }
                else if (position==5) {
                    Intent intent=new Intent(getApplicationContext(),ActivityContactUs.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Prepare some dummy data for grid_view
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);

        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, "Image#" + i));
        }
        return imageItems;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onResume fetching value from shared_preference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        //Fetching the boolean value form shared_preferences
        email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "");
    }

    // Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

//        final MenuItem menuItem=menu.findItem (R.id.action_bar_notification);
//
//        View actionView = MenuItemCompat.getActionView(menuItem);
//        textCartItemCount = actionView.findViewById(R.id.cart_badge);
//
//        setupBadge();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.action_bar_notification:
                    Intent intent=new Intent (this,ActivityNotification.class);
                    intent.putExtra ("Email_ID",email);
                    startActivity (intent);
                break;
            case R.id.logout:
                logout();
                break;
        }
        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 100)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Getting out shared_preferences
                       preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();
                        //Putting the value false for logged_in
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");
                        //Saving the shared_preferences
                        editor.commit();
                        //Starting login activity
                        Intent intent = new Intent(ActivityDashboard.this, ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
       k++;
        if(k==1) {
            Toast.makeText(ActivityDashboard.this, "Please press again to exit ", Toast.LENGTH_SHORT).show();
        }
            else
        {
            moveTaskToBack(true);
            k=0;
        }
    }
}

