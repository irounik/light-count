package com.bits.dev;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button mButtonRecord;
    private Button mButtonShow;
    private TextView mTextView;

    private SensorManager mSensorManager;
    private Sensor mLight;

    private AppDB appDb;
    private Calendar cal;
    private int date;
    private int minutes;
    private int seconds;
    private int hours;
    public static final int COL_HOURS = 1;
    public static final int COL_MINUTES = 2;
    public static final int COL_SECONDS = 3;
    public static final int COL_LIGHT = 4;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String LOG_TAG = "SQLiteTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of the sensor service,
        // and use that to get an instance of light sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //mTemperature= mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE); // requires API level 14.
            mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // requires API level 14.
        }
        if (mLight == null) {
            // Ambient light sensor is not supported, exit...
            Log.e(LOG_TAG, "Ambient Light sensor not supported");
            finish();
        }

        // Instantiate the button, text view and edit text object
        mButtonRecord = findViewById(R.id.button_record);
        mButtonShow = findViewById(R.id.button_show);
        mTextView = findViewById(R.id.text_view);

        // Instantiate the App DB
        appDb = new AppDB(this);

        // Open the AppDB;
        appDb.open();

        // Action taken when the record button is clicked
        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Record current calendar instance.
                // If we do at the beginning, it will show that date and time only
                cal = Calendar.getInstance();

                // Record current time
                date = cal.get(Calendar.DAY_OF_MONTH);
                minutes = cal.get(Calendar.MINUTE);
                seconds = cal.get(Calendar.SECOND);
                hours = cal.get(Calendar.HOUR_OF_DAY);

                // Add time and Light intensity to the DB
                int count = getCountTillNow(date) + 1;
                appDb.insert(date, hours, minutes, seconds, count);
                sendToCloud(hours, minutes, seconds, count);
                Toast.makeText(v.getContext(), "Entry Added with Count: " + count, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Action taken when the show button is clicked
        mButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayText = "";
                // If we do at the beginning, it will show that date and time only
                cal = Calendar.getInstance();
                // Get today's date
                date = cal.get(Calendar.DAY_OF_MONTH);

                // Query the data for current date
                Cursor cursor = appDb.getAllRows(date);
                if (cursor.getCount() == 0) {
                    Toast.makeText(v.getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor.moveToFirst();
                do {
                    int hrs = cursor.getInt(COL_HOURS);
                    int mnts = cursor.getInt(COL_MINUTES);
                    int secs = cursor.getInt(COL_SECONDS);
                    int light = cursor.getInt(COL_LIGHT);
                    displayText = displayText + hrs + ":" +
                            mnts + ":" +
                            secs + " - " +
                            light + "\n";
                } while (cursor.moveToNext());

                mTextView.setText(displayText);
            }
        });
    }

    private int getCountTillNow(int date) {
        try {
            Cursor cursor = appDb.getLastRow(date);
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return 0;
            else return cursor.getInt(0);
        } catch (Exception e) {
            return 0;
        }
    }

    private void sendToCloud( int hours, int minutes, int seconds, int light) {
        Map<String, String> payload = new HashMap<>();
        payload.put("hours", String.valueOf(hours));
        payload.put("minutes", String.valueOf(minutes));
        payload.put("seconds", String.valueOf(seconds));
        payload.put("light", String.valueOf(light));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);
        String docId = sdf.format(cal.getTime());
        firestore.collection("/lightdata")
                .document(docId)
                .set(payload);
    }

}
