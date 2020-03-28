package com.example.roadtripapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ChoseLocation extends AppCompatActivity {

    Button choselocation;
    TextView textview;
    int REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_location);

        choselocation = findViewById(R.id.Picker);
        textview = findViewById(R.id.text_view);


    }
}
