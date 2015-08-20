package com.example.nick.randomize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// What to do for this Activity in general
// TODO 1. Redesign list items to be more user-friendly (hints? buttons on items?)
// TODO 2. Make a general settings page (?)


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_CHOSEN = "com.example.nick.Randomize.CHOSEN"; // position key
    public final static String EXTRA_LISTS = "com.example.nick.Randomize.LISTS"; // lists key
    private ArrayList<RandomizeList> arrayList; // list of all RandomizeLists
    private ArrayAdapter adapter; // adapter to handle listView
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        arrayList = loadLists();


        // grab edited version of lists from EditList if applicable
        Intent intent = getIntent();
        if (intent.hasExtra(EditList.EXTRA_SAVED)) {
            // grab the list of RandomizeLists
            arrayList = intent.getParcelableArrayListExtra(EditList.EXTRA_SAVED);

            // save it
            saveLists(arrayList);
        }

        if (arrayList.isEmpty())
            Toast.makeText(getApplicationContext(),
                    "Press the add button to add a list.",
                    Toast.LENGTH_SHORT).show();

        // set us up the UI
        listView = (ListView) findViewById(R.id.listview);
        adapter = new CustomArrayAdapterMain(this, arrayList);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // when item in list is clicked:
                Intent intent = new Intent(getApplicationContext(), RandomizeActivity.class);
                intent.putExtra(EXTRA_CHOSEN, position); // position in array
                intent.putExtra(EXTRA_LISTS, arrayList); // all RandomizeLists in array
                startActivity(intent); // send to RandomizeActivity for randomization
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
            {
                // when item in list is long clicked
                Intent intent = new Intent(getApplicationContext(), EditList.class);
                intent.putExtra(EXTRA_CHOSEN, position); // position in array
                intent.putExtra(EXTRA_LISTS, arrayList); // all RandomizeLists in array
                startActivity(intent); // send to EditList for editing

                return true; // probably a bad idea
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO settings?
            return true;
        } else if (id == R.id.action_new) { // user wants a new list
            showNewListDialog();
        }


        return super.onOptionsItemSelected(item);
    }


    // loadLists(): function for loading all RandomizeLists from memory.
    //              Currently creates new list and saves a new file if a file was not found
    private ArrayList<RandomizeList> loadLists() {
        ArrayList<RandomizeList> lists = new ArrayList<RandomizeList>();
        File file = new File(this.getFilesDir(), "lists"); // "lists" will be the only file we need
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lists = (ArrayList<RandomizeList>) ois.readObject();
            if (lists == null)
                throw new Exception("Null list read from storage"); // for debugging
            ois.close();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Log.i("MainActivity", "FileNotFoundException");
                saveLists(lists); // save ArrayList of RandomizeLists to be loaded again
                Log.i("MainActivity", "Made new 'lists' file\n");
                lists = loadLists(); // load again for consistency
            } else
                e.printStackTrace();
        }

        return lists;
    }

    // saveLists(): function for saving ArrayList of RandomizeLists to our master "lists" file in
    //              internal storage
    public void saveLists(ArrayList<RandomizeList> lists) {
        try {
            FileOutputStream fos = openFileOutput("lists", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            if(lists == null)
            {
                lists = new ArrayList<>();
            }

            oos.writeObject(lists);

            oos.close();
            Log.i("MainActivity", "Lists saved.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNewListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final EditText edittext = new EditText(this);
        builder.setTitle("");
        builder.setMessage("Enter your title:");
        edittext.setHint("Your title here...");

        builder.setPositiveButton("Create List", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // sanitizing input
                String sanit = edittext.getText().toString().trim();

                if (sanit != null && !sanit.equals("")) {
                    RandomizeList newList = new RandomizeList(edittext.getText().toString());
                    arrayList.add(newList);
                    Intent intent = new Intent(getApplicationContext(), EditList.class);
                    intent.putExtra(EXTRA_CHOSEN, arrayList.size() - 1); // end of arrayList
                    intent.putParcelableArrayListExtra(EXTRA_LISTS, arrayList); // all RandomizeLists
                    startActivity(intent); // send to EditList for editing
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Bad or missing title. Please try again.", Toast.LENGTH_SHORT)
                    .show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();

        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alert.getWindow().setSoftInputMode(WindowManager.
                            LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alert.setView(edittext);

        alert.show();
    }
}
