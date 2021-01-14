package com.baldware.intolerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.BitmapHandler;
import com.baldware.intolerapp.json.JSONHandler;

public class PictureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("title"));

        ImageView imageView = findViewById(R.id.picture_image_view);

        if(JSONHandler.getImage() != null) {
            imageView.setImageBitmap(BitmapHandler.createShowable(JSONHandler.getImage()));
        }
    }
}
