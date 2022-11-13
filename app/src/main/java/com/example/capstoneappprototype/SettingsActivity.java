package com.example.capstoneappprototype;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Class revolves around dealing with settings menu
public class SettingsActivity extends AppCompatActivity{

        @Override
        // Starts in-app settings
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings_page);
        }
}
