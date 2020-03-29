package com.example.roadtripapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {
    public static final int CONTACT_ACTIVITY_REQUEST_CODE = 0;
    static final int MAP_REQUEST_CODE = 1;
    public String Lat;
    public String Long;
    public TextView number_text;
    public TextView location_text;
    public TextView lat_text;
    public TextView long_text;
    public Button btnContact;
    public Button btnSend;
    public Button btnLocation;
    public String number;
    public String location;
    public String name;
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
        lat_text = (TextView) findViewById(R.id.tvLat);
        long_text = (TextView) findViewById(R.id.tvLongitude);

        btnContact.setOnClickListener(clicker);
        btnLocation.setOnClickListener(clicker);
        btnSend.setOnClickListener(clicker);

        //Get name of owner of phone owner for message later
        
            GetNameDialog();

    }
    private View.OnClickListener clicker = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLocation:
                    location = "Test Location";
                    Intent mapsfeature = new Intent(getApplicationContext(), MapsFeature.class);
                    startActivity(mapsfeature);
                    break;
                case R.id.btnContact:
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContact, CONTACT_ACTIVITY_REQUEST_CODE);
                    break;
                case R.id.btnSend:
                    //We can remove this eventually this button was just for debugging the sms
                    if(number != null) {
                    String message = name + " has reached the destination of " + location + "!";
                    sendSMS(number, message);
                    break;
                }
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
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {

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

}

