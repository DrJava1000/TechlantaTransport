package com.example.capstoneappprototype;

import android.app.AlertDialog;
import android.os.*;
import androidx.appcompat.app.*;
import android.content.*;
import android.os.CountDownTimer;
import android.view.View;
import java.util.*;

public class MainActivity extends AppCompatActivity{

    private AlertDialog.Builder emerLocateBuilder;
    private AlertDialog.Builder sendDataDialogBuilder;
    private AlertDialog sendDataDialog;

    @Override
    // Initialize MainActivity before visible to user
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        setupMainPageIcons();

        // exit status is false normally but when an exit is specified through an intent we don't want to reload the login process as it tries to exit and reload the main activity
        if(!AuthenticationActivity.getAppExitStatus()) {
            startActivity(new Intent(this, AuthenticationActivity.class)); // start login process
        }

        // Checks for intent to kill the app entirely (set in login/authentication activity), which exists, and kills the main activity to kill the app
        // NOTE: app still shows up on android os (exit from there to officially shut down the app as android is strict when it comes to apps killing themselves)
        if(getIntent().getBooleanExtra("EXIT APP", false))
        {
            finish();
        }
    }

    // Initialize handlers for app home screen icons
    private void setupMainPageIcons()
    {
        final HashMap<View, Class<?>> iconActivityBinds = new HashMap<>(); // create HashMap (button as key and value as activity's runtime class)

        // Associate button to handler activity class
        iconActivityBinds.put(findViewById(R.id.settings_icon), SettingsActivity.class);
        iconActivityBinds.put(findViewById(R.id.shopping_cart_icon), AdsActivity.class);
        iconActivityBinds.put(findViewById(R.id.traffic_light_icon), UpdatesActivity.class);

        Iterator<View> mainPageIcons = iconActivityBinds.keySet().iterator(); // create iterator for buttons

        // Define button handlers for each button (key) using its associated value
        // currentIcon is key and iconActivityBinds.get(currentIcon) is value
        while(mainPageIcons.hasNext())
        {
            final View currentIcon = mainPageIcons.next();
            currentIcon.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        startActivity(new android.content.Intent(MainActivity.this, iconActivityBinds.get(currentIcon)));
                    }

                });

        }

        setupEmergencyLocateBtnListener(); // registers main page emergency icon to bring up a dialog
        buildEmerLocateDialog(); // build main page emergency icon functionality
    }

    // sets up main page emergency icon to prompt user to send location data to emergency services via a dialog
    private void setupEmergencyLocateBtnListener()
    {
        findViewById(R.id.emergency_icon).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                emerLocateBuilder.show();
            }
        });
    }

    // builds send emergency services location data dialog
    // Built here once and is only shown via selecting emergency locate icon (less work for the garbage collector -> may not be necessary)
    private void buildEmerLocateDialog()
    {
        emerLocateBuilder = new AlertDialog.Builder(this);
        emerLocateBuilder.setMessage("Send location data to emergency services.");

        emerLocateBuilder.setNegativeButton("Don't send", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                System.out.println("/'Don't Send/' clicked.");
            }
        });

        emerLocateBuilder.setPositiveButton("Send data", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                buildTempSendLocateProgressDialogs();
            }
        });
    }

    // builds temporary "sending data" and "data sent" dialogs to give confirmation to the user that location data is being sent to emergency services
    // fix (dialogs are being shown properly)
    private void buildTempSendLocateProgressDialogs()
    {
        sendDataDialogBuilder = new AlertDialog.Builder(this);
        System.out.println("Before new dialog");
        sendDataDialog = sendDataDialogBuilder.setMessage("Sending data...").show();

        setSentDataDialogHiddenTimer(); // set 3 second timer

        sendDataDialog.dismiss(); // hide dialog

    }

    // set and start timer to bring up "Data sent" message after 3 seconds
    private void setSentDataDialogHiddenTimer()
    {
        new CountDownTimer(1500,1000)
        {
            public void onFinish()
            {
                sendDataDialog = sendDataDialogBuilder.setMessage("Data sent. Please standby, help is on the way.").show();
            }

            public void onTick(long tick)
            {}
        }.start();

    }
}


