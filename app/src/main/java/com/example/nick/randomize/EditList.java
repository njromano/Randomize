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

import java.util.ArrayList;


public class EditList extends ActionBarActivity {

    private List chosenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // fetch intent from MainActivity
        Intent intent = getIntent();
        chosenList = (List) intent.getParcelableExtra(MainActivity.EXTRA_CHOSEN);

        // set activity title
        setTitle(chosenList.title);

        ArrayList<String> editList = new ArrayList<String>();
        editList = chosenList.listItems;

        ListView listView = (ListView) findViewById(R.id.listeditview);

        // set up ArrayAdapter to capture the array
        ArrayAdapter editAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, editList);
        listView.setAdapter(editAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "You clicked item" + position, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.action_edit_title)
        {
            // TODO open title edit dialog
        }

        return super.onOptionsItemSelected(item);
    }
}
