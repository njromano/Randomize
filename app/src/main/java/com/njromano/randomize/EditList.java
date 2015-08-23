package com.njromano.randomize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.njromano.randomize.R;

import java.util.ArrayList;

public class EditList extends AppCompatActivity {
    // key for parcel passing
    public final static String EXTRA_SAVED = "com.example.nick.Randomize.SAVED";
    // ArrayList of all lists
    private ArrayList<RandomizeList> arrayList;
    private RandomizeList chosenList;
    private int chosenIndex;
    private CustomArrayAdapter editAdapter;
    private boolean dataChanged;
    private int backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        dataChanged = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fetch intent from MainActivity
        try {
            Intent intent = getIntent();
            arrayList = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LISTS);
            chosenIndex = intent.getIntExtra(MainActivity.EXTRA_CHOSEN, 0);
            // check if it's a valid object
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

        /*
        // debug info, dumping done items
        String debug = new String("\n");
        for(int i=0; i<chosenList.itemsDone.size();i++)
        {
            debug = debug + chosenList.itemsDone.get(i) + "\n";
        }
        Log.d("EditList", "Done list dump: \n" + debug);
        */


        ListView listView = (ListView) findViewById(R.id.listeditview);

        // add header to listView
        View titleBox = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.title_box,listView,false);
        listView.addHeaderView(titleBox);

        // set up CustomArrayAdapter to capture the array
        editAdapter = new CustomArrayAdapter(this, chosenList.listItems, chosenList.itemsDone);
        listView.setAdapter(editAdapter);


        // set us up the UI
        TextView titleText = (TextView) this.findViewById(R.id.listTitle);
        titleText.setText(chosenList.title);
        titleText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showEditTitleDialog();
                editAdapter.notifyDataSetChanged();
                return true;
            }
        });

        ImageButton editTitle = (ImageButton) this.findViewById(R.id.titleEdit);
        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog();
                editAdapter.notifyDataSetChanged();
            }
        });

        // handle keyboard events to show/hide the title when adding items
        /*
        EditText newItemText = (EditText) findViewById(R.id.newitemtext);
        newItemText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                View titleBox = findViewById(R.id.titleBox);
                if (hasFocus && imm.isActive())
                {
                    titleBox.setVisibility(View.GONE);
                }
                else if (titleBox.getVisibility() == View.GONE)
                {
                    titleBox.setVisibility(View.VISIBLE);
                }
            }
        });
        */
    }

    @Override
    public void onBackPressed()
    {
        if (dataChanged || editAdapter.dataChanged)
            showBackConfirmationDialog();
        else
            super.onBackPressed();
    }

    @Override
    public void onResume()
    {
        super.onResume();
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

        if (id == R.id.action_save && !chosenList.listItems.isEmpty()) {
            // we can save the items we put in
            // set up intent
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            // update our master list and put it into the intent
            arrayList.set(chosenIndex, chosenList);
            intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // vamanos
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_save && chosenList.listItems.isEmpty()){
            // we shouldn't be saving if we don't have anything to save. Notify the user.
            Toast.makeText(getApplicationContext(),
                    "Please add some items into your list before saving it.",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
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

    // respond to new item saved
    public void addItem(View view)
    {
        // let the activity know something has changed
        dataChanged = true;

        // grab the text for that item
        EditText newItem = (EditText) findViewById(R.id.newitemtext);
        // sanitize it
        String sanit = newItem.getText().toString().trim();
        // check if it's valid
        if(sanit != null && !sanit.equals(""))
        {
            chosenList.addItem(newItem.getText().toString());
            editAdapter.notifyDataSetChanged();
        }
        else
        {
            // ask the user to try again
            Toast.makeText(getApplicationContext(),
                    "Bad or missing item text. Please try again.", Toast.LENGTH_SHORT)
                    .show();
        }

        // clear the text in the view
        newItem.setText("");

        /*
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newItem.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        newItem.clearFocus();
        */

    }

    // show dialog for editing the title of the list
    public void showEditTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        builder.setTitle("");
        builder.setMessage("Edit your title:");
        edittext.setText(chosenList.title);

        // make sure we select all the text inside the edittext view when we get here
        edittext.setSelectAllOnFocus(true);

        builder.setPositiveButton("Save Title", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // check sanity
                String sanit = edittext.getText().toString().trim();
                if (sanit != null && !sanit.equals("")) {
                    // we can save
                    chosenList.title = edittext.getText().toString();
                    TextView titleText = (TextView) findViewById(R.id.listTitle);
                    titleText.setText(chosenList.title);
                    dataChanged = true;
                } else {
                    // notify that the user is a bad user. BAD!
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

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittext.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        final AlertDialog alert = builder.create();

        // make sure the soft input shows up when we get here
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

    // make sure user really wants to delete their precious list
    public void showDeleteConfirmationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("Are you sure you want to delete?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                arrayList.remove(chosenIndex);
                intent.putParcelableArrayListExtra(EXTRA_SAVED, arrayList);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dataChanged = true;
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

    private void showBackConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("Discard changes?");
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
