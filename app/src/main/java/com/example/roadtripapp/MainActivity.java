package com.example.roadtripapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    public static final int CONTACT_ACTIVITY_REQUEST_CODE = 0;
    static final int MAP_REQUEST_CODE = 1;
    public Double LatDest = null;
    public Double LongDest = null;
    public Double LatCurr = null;
    public Double LongCurr = null;
    public TextView number_text;
    public TextView location_text;
    public TextView lat_textDest;
    public TextView long_textDest;
    public TextView lat_textCurr;
    public TextView long_textCurr;
    public Button btnContact;
    public Button btnSend;
    public Button btnLocation;
    public String number;
    public String location;
    public String name;
    public int Count = 0;
    public Double distance;
    public static final Double LOCATION_DISTANCE_CHECK = 0.1; //needs to be in kilometers - eg this is 100 m

    private SimpleLocation mlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PROFILE}, PackageManager.PERMISSION_GRANTED);
        number = null;
        name = null;
        number_text = (TextView) findViewById(R.id.tvNumber);
        location_text = (TextView) findViewById(R.id.tvLocation);
        btnContact = (Button) findViewById(R.id.btnContact);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        btnSend = (Button) findViewById(R.id.btnSend);
        lat_textDest = (TextView) findViewById(R.id.tvLatDest);
        long_textDest = (TextView) findViewById(R.id.tvLongitudeDest);
        lat_textCurr = (TextView) findViewById(R.id.tvLatCurr);
        long_textCurr = (TextView) findViewById(R.id.tvLongitudeCurr);

        btnContact.setOnClickListener(clicker);
        btnLocation.setOnClickListener(clicker);
        btnSend.setOnClickListener(clicker);


        // construct a new instance of SimpleLocation
        boolean requireFineGranularity = true;
        boolean passiveMode = false;
        long updateIntervalInMilliseconds = 3*1000;

        mlocation = new SimpleLocation(this, requireFineGranularity, passiveMode, updateIntervalInMilliseconds);

        // if we can't access the location yet
        if (!mlocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
        mlocation.beginUpdates();
        createNotificationChannel();
        //Get name of owner of phone owner for message later
        GetNameDialog();




    }
    private View.OnClickListener clicker = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLocation:
                    Intent mapsfeature = new Intent(getApplicationContext(), MapsFeature.class);
                    startActivityForResult(mapsfeature,MAP_REQUEST_CODE);
                    break;
                case R.id.btnContact:
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContact, CONTACT_ACTIVITY_REQUEST_CODE);
                    break;
                case R.id.btnSend:
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0);
                    }
                    if((number != null) && (LongDest != null) && (LatDest != null)) {
                        location_text.setText("Trip Started");
                        new checkdist().execute();
                    }
                    else{
                        location_text.setText("NO location set");
                    }
                    break;
                default:
                    break;

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_ACTIVITY_REQUEST_CODE) {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                number = c.getString(phoneIndex);

                number_text.setText("The Contact Number is: " + number);
            }
        }
        if (requestCode == MAP_REQUEST_CODE) {
            //TJ, all you should need to change in here is to set LatDest/LongDest to the values from the map activity

            // CODE NEVER GETS INTO THIS IF STATEMENT, HOW CAN I GET THE MAPREQUEST CODE TO "GO" WHEN I PRESS THE PICK LOCATION
            // BUTTON IN THE MAP ACTIVITY ??????


            LatDest = data.getDoubleExtra("LAT",0);
            LongDest = data.getDoubleExtra("LONG",0);

            location_text.setText("Location Set");
            lat_textDest.setText(Double.toString(LatDest));
            long_textDest.setText(Double.toString(LongDest));
        }

    }

    public void sendSMS(String number_to_send, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number_to_send, null, message, null, null);
    }

    public void GetNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = input.getText().toString();
            }
        });
    builder.show();
    }

    public Double check_distance(){
        if ((LatCurr != null) && (LongCurr != null)) {
            // The math module contains a function
            // named toRadians which converts from
            // degrees to radians.
            Double lon1 = Math.toRadians(LongCurr);
            Double lon2 = Math.toRadians(LongDest);
            Double lat1 = Math.toRadians(LatCurr);
            Double lat2 = Math.toRadians(LatDest);

            // Haversine formula
            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.pow(Math.sin(dlat / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.pow(Math.sin(dlon / 2), 2);

            double c = 2 * Math.asin(Math.sqrt(a));

            // Radius of earth in kilometers. Use 3956
            // for miles
            double r = 6371;

            // calculate the result
            return (c * r);
        }
        else{
            return 10000.0;
        }
    }

    public void sendNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel")
                .setSmallIcon(R.drawable.car)
                .setContentTitle("Destination Text Sent")
                .setContentText("Your arrival at your Destination has been sent to " + number)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your arrival at your Destination has been sent to " + number))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(6, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Default Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }






    private class checkdist extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            int num = 0;

            boolean location_reached = false;

            while(location_reached == false) {
                //distRun.run();
                distance = check_distance();
                LatCurr = mlocation.getLatitude();
                LongCurr = mlocation.getLongitude();
                //LatCurr = mlocation.getLatitude();
                //LongCurr = mlocation.getLongitude();
                //getLocation();
                if( num %50 == 0)
                    publishProgress("hi");
                num = num+1;
                //if(LatCurr != null) {
                //lat_textCurr.setText(Double.toString(LatCurr));
                //long_textCurr.setText(Double.toString(LongCurr));
                //}
                if (distance < LOCATION_DISTANCE_CHECK)
                    location_reached = true;
            }
            return "Hi";
        }
        protected void onProgressUpdate(String... progress) {
            lat_textCurr.setText(Double.toString(LatCurr));
            long_textCurr.setText(Double.toString(LongCurr));
        }
        protected void onPostExecute(String result) {
            location_text.setText("Destination Reached");
            sendNotification();
            String message = name + " has reached their destination!";
            sendSMS(number, message);
        }

    }

}



