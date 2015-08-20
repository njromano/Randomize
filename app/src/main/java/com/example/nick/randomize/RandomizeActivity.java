package com.example.nick.randomize;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class RandomizeActivity extends ActionBarActivity {
    private ArrayList<RandomizeList> arrayList;
    private RandomizeList chosenList;
    private int chosenIndex;
    private int randCount;
    private ArrayAdapter editAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);

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

        String debug = new String();
        for(int i=0; i<chosenList.itemsDone.size();i++)
        {
            debug = debug + chosenList.itemsDone.get(i) + "\n";
        }
        Log.d("EditList", "Done list dump: " + debug);

        // set activity title
        setTitle(chosenList.title);
        final TextView textView = (TextView) this.findViewById(R.id.randomText);
        textView.setText("Tap to Randomize");

        final TextView randView = (TextView) this.findViewById(R.id.randCount);
        randCount = 0;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.randomizeLayout);
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // check if there is a valid item to choose
                boolean itemAvailable = false;
                for(String item : chosenList.itemsDone)
                {
                    if (item.equals("false")) {
                        itemAvailable = true;
                        break;
                    }
                }

                if (itemAvailable) {
                    String randSelect = new String();
                    randSelect = chosenList.getRandom();
                    textView.setText(randSelect);
                    randView.setText("Number of Randomizations: " + ++randCount);
                }
                else
                {
                    textView.setText("All done!");
                }
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
