package com.example.nick.randomize;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class EditList extends ActionBarActivity {
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    private ArrayList<RandomizeList> arrayList;
    private RandomizeList chosenList;
    private int chosenIndex;
    private ArrayAdapter editAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // fetch intent from MainActivity
        try {
            Intent intent = getIntent();
            arrayList = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTS);
            chosenIndex = intent.getIntExtra(MainActivity.EXTRA_CHOSEN, 0);
            if (arrayList != null) {
                chosenList = arrayList.get(chosenIndex);
            } else {
                throw new Exception("Null list array passed to EditList");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // set activity title
        setTitle(chosenList.title);

        ArrayList<String> editList = new ArrayList<String>();
        editList = chosenList.listItems;

        ListView listView = (ListView) findViewById(R.id.listeditview);

        // set up ArrayAdapter to capture the array
        editAdapter = new CustomArrayAdapter(this, editList);
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
    public void onResume()
    {
        super.onResume();
        if (chosenList.title.equals("New List Title"))
        {
            Toast.makeText(getApplicationContext(),
                    "Edit the title of your list by tapping the edit icon.",
                    Toast.LENGTH_SHORT).show();
        }
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
            // TODO edit settings?
            return true;
        } else if (id == R.id.action_save && !chosenList.listItems.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            arrayList.set(chosenIndex, chosenList);
            intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_save && chosenList.listItems.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "Please add some items into your list before saving it.",
                    Toast.LENGTH_SHORT)
                    .show();
        } else if (id == R.id.action_edit_title) {
            showEditTitleDialog();
            editAdapter.notifyDataSetChanged();
        } else if (id == R.id.action_delete) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            arrayList.remove(chosenIndex);
            intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // respond to new item saved
    public void addItem(View view)
    {
        EditText newItem = (EditText) findViewById(R.id.newitemtext);
        chosenList.addItem(newItem.getText().toString());
        editAdapter.notifyDataSetChanged();
        newItem.setText("");
    }

    public void showEditTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final EditText edittext = new EditText(this);
        builder.setTitle("");
        builder.setMessage("Enter your new title");
        edittext.setText(chosenList.title);
        edittext.setSelectAllOnFocus(true);

        builder.setPositiveButton("Save Title", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                chosenList.title = edittext.getText().toString();
                setTitle(chosenList.title);
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

    // wrapper function for checking if this RandomizeList has any elements in it
    private boolean listIsEmpty()
    {
        return chosenList.listItems.isEmpty();
    }
}
