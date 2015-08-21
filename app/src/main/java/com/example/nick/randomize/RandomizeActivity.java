package com.example.nick.randomize;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class RandomizeActivity extends ActionBarActivity {
    // key for passing parcels
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    private ArrayList<RandomizeList> arrayList;
    private RandomizeList chosenList;
    private int chosenIndex;
    private int chosenRandom; // random number that gets generated
    private int randCount; // number of times we have generated a result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomize);

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

        // debugging for itemsDone
        String debug = new String();
        for(int i=0; i<chosenList.itemsDone.size();i++)
        {
            debug = debug + chosenList.itemsDone.get(i) + "\n";
        }
        Log.d("EditList", "Done list dump: \n" + debug);

        // set activity title
        setTitle("Item Chooser - " + chosenList.title);

        // set us up the UI
        final TextView textView = (TextView) this.findViewById(R.id.randomText);
        final TextView randView = (TextView) this.findViewById(R.id.randCount);
        final Button doneButton = (Button) this.findViewById(R.id.doneButton);
        randCount = 0;

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.randomizeLayout);
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
                    textView.setText("All done. Don't forget to save!");
                    // make the button go away
                    doneButton.setVisibility(View.GONE);
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the itemsDone selection to "true"
                chosenList.itemsDone.set(chosenRandom, "true");
                // clearly show that the items is now marked "done"
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Toast.makeText(getApplicationContext(),
                        "Item marked \"Done\" in your list.", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.action_save) {
            // similar to other save. just set the master arrayList back up and pass it back to Main
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            arrayList.set(chosenIndex, chosenList);
            intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
