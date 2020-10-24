package com.baldware.intolerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CreationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        JSONHandler.startUpload("http://intolerapp.com/austria_upload_service.php");
    }
}
