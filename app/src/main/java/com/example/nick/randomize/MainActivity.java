package com.example.nick.randomize;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_CHOSEN = "com.example.nick.Randomize.CHOSEN";
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    private ArrayList<List> arrayList;
    private ArrayAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = loadLists();

        // set up ListView to display the list items
        listView = (ListView) findViewById(R.id.listview);

        // set up ArrayAdapter to capture the array
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditList.class);
                List chosen = arrayList.get(position);
                intent.putExtra(EXTRA_CHOSEN, (Parcelable) chosen);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // grab saved List if applicable
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SAVED)) {
            // grab the List object
            List grabbedList = intent.getParcelableExtra(EXTRA_SAVED);

            // add it to the list if it's not there
            arrayList = loadLists();
            if (!arrayList.contains(grabbedList))
                arrayList.add((List) intent.getParcelableExtra(EXTRA_SAVED));
            else {
                arrayList.set(arrayList.indexOf(grabbedList), grabbedList);
            }

            // save it
            saveLists(arrayList);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

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
            return true;
        } else if (id == R.id.action_new) {
            Intent intent = new Intent(getApplicationContext(), EditList.class);
            List chosen = new List("New List");
            intent.putExtra(EXTRA_CHOSEN, (Parcelable) chosen);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    private ArrayList<List> loadLists() {
        ArrayList<List> lists = new ArrayList<List>();
        try {
            File file = new File(this.getFilesDir(), "lists");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lists = (ArrayList<List>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                File file = new File(this.getFilesDir(), "lists");
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Log.i("MainActivity", "Made new 'lists' file\n");
            } else
                e.printStackTrace();
        }
        return lists;
    }

    private void saveLists(ArrayList<List> lists) {
        try {
            FileOutputStream fos = openFileOutput("lists", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(lists);
            oos.close();
            Log.i("MainActivity", "Lists saved.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
