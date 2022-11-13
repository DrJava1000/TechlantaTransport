package com.example.capstoneappprototype;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.*;

import androidx.core.app.ActivityCompat;
import androidx.core.content.*;

import androidx.appcompat.app.AppCompatActivity;

public class AuthenticationActivity extends AppCompatActivity {

    private AlertDialog.Builder exitDialogBuilder; // reference to exit dialog builder
    private static boolean appExit = false; // don't exit app

    @Override
    // Initialize MainActivity before visible to user
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page); // set network login page as first screen seen
        buildExitDialog();
        setLoginBtnListener();
    }


    // login button redirects to terms and conditions for user
    private void setLoginBtnListener() {
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setContentView(R.layout.term_and_cond_page); // switch to terms and conditions page
                // Can only assign listeners to elements once they enter the content view
                setAgreeBtnListener();
                setTermsScrollListener();
            }
        });
    }

    // agree button finishes user login
    private void setAgreeBtnListener() {
        findViewById(R.id.agree_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkForAndRequestLocationPermission(); // deal with location permissions and whether or not they're given
            }
        });
    }

    private void setTermsScrollListener()
    {
        View termScroller = findViewById(R.id.term_and_cond_scroll);
        termScroller.setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
         public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
             ScrollView scrollview = (ScrollView)v; // ScrollView seen as View and needs to be seen as ScrollView

             // Calculate the scroll difference
             int diff = (scrollview.getChildAt(0).getBottom()-(scrollview.getHeight()+scrollview.getScrollY()));

             // getHeight measures what's visible on the scrollView
             // getScrollY measures topmost y position of visible part of scrollview (thereby giving height of what can't be seen)
             // getChildAt(0).getBottom() get height of visible + invisible

             // if diff is zero, then the bottom has been reached
             if( diff == 0 )
             {
                 // enable agree button if terms scroller reaches bottom
                 findViewById(R.id.agree_btn).setEnabled(true);
                 findViewById(R.id.agree_btn).setAlpha(1); // make agree button more profound to user when enabled
             }
         }
        });
    }

    // Checks for Location permission and request if not given using programmer-created request code 1000
    // AVD may save permission settings across AVD shutdown
    private void checkForAndRequestLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }else
        {
          finish(); // exit login process if permission was given already on previous run of the app
        }
    }


    // Called in response to user's selection for location permission dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults)
    {
        if(requestCode == 1000)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                finish(); // exit login process if permission was given
            }
            else
            {
                sendAppKillCode(); // terminate app if permission is denied (app usage is useless without location service)
            }
        }

    }

    // Builds exit dialog for authentication activity to keep user from using app without agreeing to terms of service
    // Built here once and is only shown via onBackPressed() (less work for the garbage collector -> may not be necessary)
    private void buildExitDialog()
    {
        exitDialogBuilder = new AlertDialog.Builder(this);
        exitDialogBuilder.setMessage("Exit Techlanta Transport");

        exitDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                System.out.println("/'No/' clicked.");
            }
        });

        exitDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                sendAppKillCode();
            }
        });
    }

    // Sets in motion the process of the app to kill itself and return to android os home screen
    private void sendAppKillCode()
    {
        appExit = true; // prepare to exit app
        Intent killIntent = new Intent(this, MainActivity.class);
        killIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // set flag to kill login activity (clean activity stack) and recreate main activity (previously bottom-most activity)
        killIntent.putExtra("EXIT APP", true); // add message EXIT APP to the kill app intent
        startActivity(killIntent); // reloads main activity to kill
    }

    // find out whether the app is supposed to shut down
    public static boolean getAppExitStatus()
    {
        return appExit;
    }

    // Bring up exit dialog when back button is pressed during login and terms acceptance process
    @Override
    public void onBackPressed()
    {
        exitDialogBuilder.show();
    }
}
