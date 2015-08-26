package com.njromano.choosr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class RandomizeActivity extends AppCompatActivity {
    // key for passing parcels
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    private ArrayList<RandomizeList> arrayList;
    private RandomizeList chosenList;
    private int chosenIndex;
    private int chosenRandom; // random number that gets generated
    private int randCount; // number of times we have generated a result
    private boolean dataChanged;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 12)
            {
                findNextItem();
                Vibrator v = (Vibrator) getApplicationContext()
                        .getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get list from Intent
        try {
            Intent intent = getIntent();
            arrayList = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTS);
            chosenIndex = intent.getIntExtra(MainActivity.EXTRA_CHOSEN, 0);
            if (arrayList != null) {
                chosenList = arrayList.get(chosenIndex);
            } else {
                throw new Exception("Null list array passed to RandomizeActivity");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*
        // debugging for itemsDone
        String debug = new String();
        for(int i=0; i<chosenList.itemsDone.size();i++)
        {
            debug = debug + chosenList.itemsDone.get(i) + "\n";
        }
        Log.d("EditList", "Done list dump: \n" + debug);
        */

        // set activity title
        //setTitle("Item Randomizer - " + chosenList.title);

        // set us up the UI

        randCount = 0;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.randomizeLayout);
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
              public void onClick(View v) {
                findNextItem();
            }
        });

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.randomText);
                // set the itemsDone selection to "true"
                chosenList.itemsDone.set(chosenRandom, "true");
                // clearly show that the items is now marked "done"
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dataChanged = true;
                Toast.makeText(getApplicationContext(),
                        "Item marked \"Done\" in your list.", Toast.LENGTH_SHORT).show();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onBackPressed()
    {
        if (dataChanged)
            showBackConfirmationDialog();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_randomize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            // similar to other save. just set the master arrayList back up and pass it back to Main
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            arrayList.set(chosenIndex, chosenList);
            intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == android.R.id.home)
        {
            onBackPressed();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void findNextItem()
    {
        TextView textView = (TextView) findViewById(R.id.randomText);
        TextView randView = (TextView) findViewById(R.id.randCount);
        Button doneButton = (Button) findViewById(R.id.doneButton);
        // check if there is a valid item to choose
        boolean itemAvailable = false;
        for(String item : chosenList.itemsDone)
        {
            if (item.equals("false")) {
                itemAvailable = true;
                break;
            }
        }

        // itemAvailable should ONLY be false when we have all "true" values in itemsDone

        // we clicked, so refresh the textView and show the button
        textView.setPaintFlags(0);
        doneButton.setVisibility(View.VISIBLE);

        // the magic happens here
        // only get a random result if there is one to get
        // otherwise show the user we are done and remind them to save
        if (itemAvailable) {
            String randSelect = new String();
            chosenRandom = chosenList.getRandom();
            randSelect = chosenList.listItems.get(chosenRandom);
            textView.setText(randSelect);
            randView.setText("Number of Randomizations: " + ++randCount);
        }
        else
        {
            textView.setText("All done!");
            // make the button go away
            doneButton.setVisibility(View.GONE);
        }
    }

    private void showBackConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("You have unsaved marked items. Are you sure you want to discard them?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();

        alert.show();
    }
}
