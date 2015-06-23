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
    public final static String EXTRA_LISTS = "com.example.nick.Randomize.LISTS";
    private ArrayList<RandomizeList> arrayList;
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
                intent.putExtra(EXTRA_CHOSEN, position);
                intent.putExtra(EXTRA_LISTS, arrayList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (intent.hasExtra(EditList.EXTRA_SAVED)) {
            // grab the List object
            arrayList = intent.getParcelableArrayListExtra(EditList.EXTRA_SAVED);

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
    protected void onStop()
    {
        super.onPause();

        adapter.notifyDataSetInvalidated();
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
            arrayList = loadLists();
            arrayList.add(new RandomizeList("New List Title"));
            intent.putExtra(EXTRA_CHOSEN, arrayList.size() - 1);
            intent.putParcelableArrayListExtra(EXTRA_LISTS, arrayList);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    private ArrayList<RandomizeList> loadLists() {
        ArrayList<RandomizeList> lists = new ArrayList<RandomizeList>();
        File file = new File(this.getFilesDir(), "lists");
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lists = (ArrayList<RandomizeList>) ois.readObject();
            if (lists == null)
                throw new Exception("Null list read from storage");
            ois.close();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Log.i("MainActivity", "FileNotFoundException");
                lists.add(new RandomizeList("New Title"));
                saveLists(lists);
                Log.i("MainActivity", "Made new 'lists' file\n");
                lists = loadLists();
            } else
                e.printStackTrace();
        }

        return lists;
    }

    private void saveLists(ArrayList<RandomizeList> lists) {
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
}
