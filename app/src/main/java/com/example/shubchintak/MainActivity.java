package com.example.shubchintak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.logging.LogWrapper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;

    private DrawerLayout mdrawerLayoout;

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0x1001;

    static TextView stepsNo;
    static TextView heartbeats;
    static Activity a;

    private static FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static DatabaseReference mNotifacationDatabase;


    Button user_notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        a = MainActivity.this;
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Shubchintak");

        stepsNo = (TextView) findViewById(R.id.main_stat);
        heartbeats = (TextView) findViewById(R.id.main_stat1);
        user_notifications = (Button) findViewById(R.id.main_notibtn);

        mdrawerLayoout = (DrawerLayout) findViewById(R.id.main_activity);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        if(ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACTIVITY_REC))

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNotifacationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            FitnessOptions fitnessOptions =
                    FitnessOptions.builder()
                            .addDataType(DataType.TYPE_HEART_RATE_BPM)
                            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY)
                            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                            .build();

            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this,
                        REQUEST_OAUTH_REQUEST_CODE,
                        GoogleSignIn.getLastSignedInAccount(this),
                        fitnessOptions);
            } else {
                subscribe();
            }

            if (checkAndRequestPermissions()) {
//            requestPermissions();
            } else {
//            buildFitnessClient();
            }

            readData();

        }
//        FitnessOptions fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
//                        .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY)
//                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                        .build();
//
//        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                    this,
//                    REQUEST_OAUTH_REQUEST_CODE,
//                    GoogleSignIn.getLastSignedInAccount(this),
//                    fitnessOptions);
//        } else {
//            subscribe();
//        }
//
//        if (checkAndRequestPermissions()) {
////            requestPermissions();
//        } else {
////            buildFitnessClient();
//        }
//
//        readData();

        mHandler = new Handler();
        startRepeatingTask();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mdrawerLayoout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawerLayoout.addDrawerListener(toggle);
        toggle.syncState();

        //---------------------------Notifications-------------------------
        user_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+919748184589", null)));
//                Map notificationData = new HashMap();
//                notificationData.put("from", mAuth.getCurrentUser().getUid());
//                notificationData.put("type", "request");
//                mNotifacationDatabase.child("jwVIcozKJdeU0jP6LHQqqYYmCd72").push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(MainActivity.this, "data aded", Toast.LENGTH_SHORT).show();
//                    }
//                });

            }
        });
        //--------------------------alarm-------------
//        final Button button = buttons[2]; // replace with a button from your own UI
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override public void onReceive( Context context, Intent _ )
//            {
//                button.setBackgroundColor( Color.RED );
//                context.unregisterReceiver( this ); // this == BroadcastReceiver, not Activity
//            }
//        };
//        this.registerReceiver( receiver, new IntentFilter("com.blah.blah.somemessage") );
//
//        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.blah.blah.somemessage"), 0 );
//        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
//
//        // set alarm to fire 5 sec (1000*5) from now (SystemClock.elapsedRealtime())
//        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*5,,);


    }


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS);
        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionState3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
//        return permissionState == PackageManager.PERMISSION_GRANTED && permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionState1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BODY_SENSORS);
        }
        if (permissionState2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionState3 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BODY_SENSORS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
                    &&perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BODY_SENSORS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed! Body sensor");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing body sensor.", task.getException());
                                }
                            }
                        });
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed! steps");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing. steps", task.getException());
                                }
                            }
                        });


    }


    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
//                        .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
//                        .bucketByTime(1, TimeUnit.MINUTES)
//                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .enableServerQueries()
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]
        return readRequest;
    }

    private void readData() {
        DataReadRequest readRequest = queryFitnessData();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    private static void readData_steps() {
        Fitness.getHistoryClient(a, GoogleSignIn.getLastSignedInAccount(a))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                String steps = "" + total;
                                stepsNo.setText(steps);
                                Log.i(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(
                    TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }

        // [END parse_read_data_result]
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        final DateFormat dateFormat = getTimeInstance();
        int a = 1;
        for (final DataPoint dp : dataSet.getDataPoints()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    readData_steps();
                    Log.i(TAG, "Data point:");
                    Log.i(TAG, "\tType: " + dp.getDataType().getName());
                    Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    for (Field field : dp.getDataType().getFields()) {
                        Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                        if (field.getName().equals("bpm")) {
                            heartbeats.setText(dp.getValue(field).toString());
                            float heart = dp.getValue(field).asFloat();
//                            Log.i(TAG,dp.getValue(field)+"" );
                            if (heart >= 80.0) {
                                myMessageHigh(heart);
                                playAlarm();


//                                Log.i(TAG,heart+"" );
                                Map notificationData = new HashMap();
                                notificationData.put("from", mAuth.getCurrentUser().getUid());
                                notificationData.put("type", "high");
                                mNotifacationDatabase.child("jwVIcozKJdeU0jP6LHQqqYYmCd72").push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "data sent");
                                    }
                                });
                            }
                            if (heart <= 75.0) {
                                myMessageLow(heart);
//                                Log.i(TAG,heart+"" );
                                playAlarm();
                                Map notificationData = new HashMap();
                                notificationData.put("from", mAuth.getCurrentUser().getUid());
                                notificationData.put("type", "low");
                                mNotifacationDatabase.child("jwVIcozKJdeU0jP6LHQqqYYmCd72").push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "data sent");
                                    }
                                });
                            }
                        }
                    }
                }




            }, 3000 * a);
            a++;

        }
    }
    private static void playAlarm() {
        MediaPlayer ring =MediaPlayer.create(a,R.raw.save);
        ring.start();
    }

    private static void myMessageHigh(float heart) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+917866807286", null, "heart rate is " + heart+"  Heartbeat is too high", null, null);
        Log.i(TAG, "Success message sent");
    }
    private static void myMessageLow(float heart) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+917866807286", null, "heart rate is " + heart+"   Heartbeat is too low", null, null);
        Log.i(TAG, "Success message sent");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case R.id.action_read_data:
                Log.i(TAG, "Entered method read data");
                readData_steps();
                break;
        }
        mdrawerLayoout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mdrawerLayoout.isDrawerOpen(GravityCompat.START)) {
            mdrawerLayoout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        }

    }

    private void sendToStart() {
        mDatabase.child("Users").child("token_id").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(startIntent);
                finish();
            }
        });


    }
    private int mInterval = 60000; // 5 seconds by default, can be changed later
    private Handler mHandler;


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
//                readData(); //this function can change value of mInterval.
                falseCaloriesIncrement();
//                Toast.makeText(MainActivity.this, "Here I am", Toast.LENGTH_SHORT).show();
//                Log.d(TAG,"I am here");
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }


    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
    int cal=57;
    TextView falseCalory;
    public void falseCaloriesIncrement(){
        cal=cal+randomNo();
//        int c=randomNo();
        Log.d(TAG,cal+"");
        falseCalory=(TextView)findViewById(R.id.main_stat2);
        falseCalory.setText(cal+"");
    }
    public int randomNo(){
        final int min = 1;
        final int max = 6;
        final int random = new Random().nextInt((max - min) + 1) + min;
//        String randi=random.toString();
        return random;
    }

}
