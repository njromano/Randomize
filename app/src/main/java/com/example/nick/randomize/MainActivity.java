package com.example.nick.randomize;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_CHOSEN = "com.example.nick.Randomize.CHOSEN";
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    public ArrayList<List> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for testing before storage implementation
        ArrayList<String> testItems1 = new ArrayList<String>();
        testItems1.add("List item 1");
        testItems1.add("List item 2");
        testItems1.add("List item 3");

        ArrayList<String> testItems2 = new ArrayList<String>();
        testItems2.add("List item 1");
        testItems2.add("List item 2");
        testItems2.add("List item 3");

        List list1 = new List("List 1 Title", testItems1);
        List list2 = new List("List 2 Title", testItems2);
        arrayList = new ArrayList<List>();
        // grab saved List if applicable
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_SAVED))
        {
            arrayList.add((List) intent.getParcelableExtra(EXTRA_SAVED));
        }
        arrayList.add(list1);
        arrayList.add(list2);
        // end test initialization

        // TODO make ArrayList load from storage (and probably stop instantiating things here)
        //ArrayList<List> arrayList = List.loadData(this);

        // set up ListView to display the list items
        ListView listView = (ListView) findViewById(R.id.listview);

        // set up ArrayAdapter to capture the array
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditList.class);
                List chosen = arrayList.get(position);
                intent.putExtra(EXTRA_CHOSEN, chosen);
                startActivity(intent);
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
            return true;
        } else if (id == R.id.action_new) {
            Intent intent = new Intent(getApplicationContext(), EditList.class);
            List chosen = new List("New List");
            intent.putExtra(EXTRA_CHOSEN, chosen);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}
