package com.example.ebc003.pathologyapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ActivityTestDetails extends AppCompatActivity implements View.OnClickListener {

    String TAG=ActivityTestDetails.class.getSimpleName();
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_CHOOSER_IMAGE_REQUEST_CODE = 101;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Pathology Camera";
    private Uri fileUri; // file url to store image/video
    private ImageView imgPreview;
    private ImageButton btnCapturePicture;
    Button mButtonUpload;
    Button mChooseTest;
    private Boolean upflag = false;
    //Radio Button
    RadioGroup inputTest;
    String var = "pre test";
    //ListView main_menu
    ListView mListViewItem;
    ArrayList<StateVO> listVOs;
    private JSONArray result;
    private ArrayList<String> students;
    Object[] mStringArray;
    String time;
    //date & time
    EditText mEditTextDate;
    EditText mEditTextTime;
    Button mButtonDate;
    Button mButtonTime;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    //shared preferences
    String emailSharedPreference;
    //progress dialog box
    private ProgressDialog pDialog;
    //User bundle info
    String firstName;
    String middleName;
    String lastName;
    String fullName;
    String personal_email;
    String date_of_birth;
    String contact_number;

    //storage path
    static String mediaPath;
    static String filePath;
    static String fileFinalPath;
    private static final String TAG_SUCCESS = "success";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_READ_EXTERNAL_STORAGE = 2;
    String imageUrl;

    //server path
    JSONParser jsonParser = new JSONParser();
    private static String url_create_patient_details = "http://www.ebusinesscanvas.com/pathalogy_lab/make_an_apt.php";
    private static String server_image_path = "http://www.ebusinesscanvas.com/pathalogy_lab/uploads_make_an_appointment";

    StringBuilder str;
    private Toast toast;
    private Thread thread;
    String mDisplayTestName;
    TextView mDisplayTest;
    String mCharAt="/";
    String mGetStringName;
    Toolbar toolbar;

    public String URL="http://www.ebusinesscanvas.com/pathalogy_lab/make_appointment_history.php";

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
        setContentView(R.layout.activity_test_details);

        imgPreview =findViewById(R.id.imageView1);
        btnCapturePicture =findViewById(R.id.photoButton);
        mButtonUpload =findViewById(R.id.btn_upload);
        inputTest =findViewById(R.id.radiotest);
        mEditTextDate=findViewById(R.id.in_date);
        mEditTextTime=findViewById(R.id.in_time);
        mButtonDate=findViewById(R.id.btn_date);
        mButtonTime=findViewById(R.id.btn_time);
        mChooseTest=findViewById(R.id.chooseTest);
        mDisplayTest=findViewById(R.id.txtTestDisplay);

        toolbar=findViewById (R.id.test_detailsToolbar);
        setSupportActionBar (toolbar);
        ActionBar actionBar=getSupportActionBar ();

        if (actionBar!=null){
            actionBar.setTitle (R.string.test_details);
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        inputTest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.radiopretest:
                        var = "Pre test";
                        alertBox(var);
                        break;
                    case R.id.radioposttest:
                        var = "Post test";
                        alertBox(var);
                        break;
                    case R.id.radiopostrandom:
                        var = "Any";
                        alertBox(var);
                        break;
                }
            }
        });

        btnCapturePicture.setOnClickListener(this);
        mButtonDate.setOnClickListener(this);
        mButtonTime.setOnClickListener(this);
        mButtonUpload.setOnClickListener(this);
        mChooseTest.setOnClickListener(this);

        listVOs = new ArrayList<>();
        students = new ArrayList<String>();
        getData();


        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        //User Bundle info
        Bundle b = getIntent().getExtras();
        firstName = b.getString("first_name");
        middleName = b.getString("middle_name");
        lastName = b.getString("last_name");
        fullName = firstName + " " + middleName + " " + lastName;
        personal_email = b.getString("email");
        date_of_birth = b.getString("date_of_birth");
        contact_number = b.getString("contact_number");

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        emailSharedPreference = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_date:{
                if (ContextCompat.checkSelfPermission(ActivityTestDetails.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityTestDetails.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(ActivityTestDetails.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {
                    ActivityCompat.requestPermissions(ActivityTestDetails.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mEditTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;
            }
            case R.id.btn_time:{
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mEditTextTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;
            }
            case R.id.photoButton:{
                checkRunTimePermission();
                break;
            }
            case R.id.btn_upload:{
                checkValidation();
                break;
            }

            case R.id.chooseTest:{
                showMultiChoiceLongItems();
            }
        }
    }

    private void checkValidation() {
        if (mEditTextDate.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please select date",Toast.LENGTH_LONG).show();
        }
        else if (mEditTextTime.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please select time",Toast.LENGTH_LONG).show();
        }
        else if (imgPreview.getDrawable()==null){
            Toast.makeText(getApplicationContext(),"Please select photo",Toast.LENGTH_LONG).show();
        }
        else if (var.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please select test",Toast.LENGTH_LONG).show();
        }
        else {
            new DoFileUpload().execute();
        }
    }

    private void checkRunTimePermission() {
        if (ContextCompat.checkSelfPermission(ActivityTestDetails.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityTestDetails.this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(ActivityTestDetails.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);
            }
        } else {
            ActivityCompat.requestPermissions(ActivityTestDetails.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        captureImage();

                } else {
                    Toast.makeText(getApplicationContext(), "Sorry you don't use camera", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void showMultiChoiceLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.test_choose)
                .items(students)
                .itemsCallbackMultiChoice(
                        new Integer[] {},
                        (dialog, which, text) -> {
                            str = new StringBuilder();
                            for (int i = 0; i < which.length; i++) {
                                if (i > 0) {
                                    str.append(",");
                                }
                                str.append(text[i]);
                            }
                            displayTextView(str.toString());
                            return true; // allow selection
                        })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    private void displayTextView(String str) {
        this.mDisplayTestName=str;
        if (str.isEmpty()){
            mDisplayTest.setText(R.string.text_choose);
        }else {
            mDisplayTest.setText(str);
        }
    }

    private void alertBox(final String var) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityTestDetails.this);
        dialog.setCancelable(false);
        dialog.setTitle("Cost of the test");
        dialog.setMessage("Value of this test is  Rs 700");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),"You Selected:-"+var,Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                        Toast.makeText(getApplicationContext(),"You Selected:-Cancel",Toast.LENGTH_LONG).show();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    // Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_view_history:
                Intent intent=new Intent (this,MakeAnAppointmentHistory.class);
                intent.putExtra ("card_view_title","Appointment History");
                intent.putExtra ("Email_ID",personal_email);
                intent.putExtra ("URL",URL);
                startActivity (intent);
                break;

            case android.R.id.home:
                Intent backMakeAppointment=new Intent (getApplicationContext (),ActivityMakeAppointment.class);
                startActivity (backMakeAppointment);
                finish ();
                break;
        }
        return true;
    }

    private void home() {
        Intent intent = new Intent(ActivityTestDetails.this, ActivityDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return getApplicationContext ().getPackageManager ().hasSystemFeature (
                PackageManager.FEATURE_CAMERA);
    }

    private void captureImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTestDetails.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent ();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent ();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }
    void galleryIntent(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                GALLERY_CHOOSER_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == GALLERY_CHOOSER_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                Uri selectedImageUri = data.getData();

                fileFinalPath = getPath(selectedImageUri, ActivityTestDetails.this);
                filePath = getPath(selectedImageUri, ActivityTestDetails.this);

                File f = new File(filePath);
                mGetStringName=f.getName ();
                filePath=mCharAt+mGetStringName;

                Log.i (TAG,"TEMP PATH FILE PATH:-"+filePath);
                Log.i (TAG,"TEMP PATH MEDIA PATH:-"+mediaPath);
                Log.i (TAG,"TEMP PATH FINAL PATH:-"+fileFinalPath);

                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(fileFinalPath, btmapOptions);
                imgPreview.setImageBitmap(bm);


            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        upflag=true;
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaPath=mediaStorageDir.getPath();
        filePath=File.separator+ "IMG_" + timeStamp + ".jpg";
        fileFinalPath=mediaPath+filePath;

        Log.i(ActivityTestDetails.class.getSimpleName(),"MEDIA_PATH"+mediaPath);
        Log.i(ActivityTestDetails.class.getSimpleName(),"FILEPATH"+filePath);
        Log.i(ActivityTestDetails.class.getSimpleName(),"FULLPATH"+fileFinalPath);

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(fileFinalPath);
        } else {
            return null;
        }

        return mediaFile;
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ActivityTestDetails.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String date = mEditTextDate.getText().toString();
            String time = mEditTextTime.getText().toString();
            imageUrl=server_image_path.concat(filePath).trim();

            List<NameValuePair> listData = new ArrayList<NameValuePair>();
            listData.add(new BasicNameValuePair("patient_full_name",fullName));
            listData.add(new BasicNameValuePair("test_type", var));
            listData.add(new BasicNameValuePair("schedule_date", date));
            listData.add(new BasicNameValuePair("schedule_time", time));
            listData.add(new BasicNameValuePair("test_name",mDisplayTestName));
            listData.add(new BasicNameValuePair("url",imageUrl));
            listData.add(new BasicNameValuePair("user_email", emailSharedPreference));
            listData.add(new BasicNameValuePair("personal_email_id", personal_email));
            listData.add(new BasicNameValuePair("date_of_birth", date_of_birth));
            listData.add(new BasicNameValuePair("contact_number", contact_number));

            JSONObject json = jsonParser.makeHttpRequest(url_create_patient_details, "POST", listData);
            Log.d(ActivityTestDetails.class.getSimpleName(), json.toString());
            listData.clear();

            Log.i(TAG,"FullName:-"+fullName);
            Log.i(TAG,"TestType:-"+var);
            Log.i(TAG,"Date:-"+date);
            Log.i(TAG,"Time:-"+time);
            Log.i(TAG,"Email:-"+emailSharedPreference);
            Log.i(TAG,"PersonEmail:-"+personal_email);
            Log.i(TAG,"DateOfBirth:-"+date_of_birth);
            Log.i(TAG,"ServerPath:-"+server_image_path);
            Log.i(TAG,"File_path:-"+filePath);
            Log.i(TAG,"IMAGE URL:-"+imageUrl);
            Log.i(TAG,"FILE FINAL PATH:-"+fileFinalPath);


            // Set your file path here
            FileInputStream fstrm = null;
            try {
                fstrm = new FileInputStream(fileFinalPath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload("http://www.ebusinesscanvas.com/pathalogy_lab/file_upload_make_an_apt.php", "ftitle", "fdescription",filePath);
            hfu.Send_Now(fstrm);
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
        protected void onPostExecute(String result) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (upflag) {
                String test_name = "";
            } else {
                Toast.makeText(getApplicationContext(), "Unfortunately file is not Uploaded..", Toast.LENGTH_SHORT).show();
            }
            if (result == "true") {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityTestDetails.this);
                dialog.setCancelable(false);
                dialog.setMessage("Hi Your Appointment is booked");
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), ActivityDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();
            }
            if (result == "false") {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityTestDetails.this);
                dialog.setCancelable(false);
                dialog.setMessage("Sorry this time slot is already booked");
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

    private void getData() {
        StringRequest stringRequest = new StringRequest(Constant.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(Constant.JSON_ARRAY);
                            getStudents(result);
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

    private void getStudents(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                students.add(json.getString(Constant.TAG_NAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
