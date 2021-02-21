package com.baldware.intolerapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baldware.intolerapp.R;

public class SettingsActivity extends AppCompatActivity {

    private boolean buttonInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new onCheckedChangeListener());
    }

    // For the radioGroup (group of radio buttons)
    public class onCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // Add the button
            if (!buttonInitialized) {
                RelativeLayout relativeLayout = findViewById(R.id.relativeLayout_settings);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.addRule(RelativeLayout.BELOW, findViewById(R.id.constraintLayout_settings).getId());

                Button button = new Button(getApplicationContext());
                button.setText(R.string.settings_button_text);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
                        saveSelection(radioButton.getText().toString());

                        Toast.makeText(getApplicationContext(), R.string.settings_applied_text, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                relativeLayout.addView(button, layoutParams);

                buttonInitialized = true;
            }
        }
    }

    private void saveSelection(String selection) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.settingsFlag), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.settingsFlag), selection);
        editor.apply();
    }
}
