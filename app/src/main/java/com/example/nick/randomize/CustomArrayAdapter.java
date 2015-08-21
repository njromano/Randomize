package com.example.nick.randomize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nick on 8/19/15 while sitting inside a nuclear shelter eating non perishables
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final ArrayList<String> done;

    public CustomArrayAdapter(Context context, ArrayList<String> values, ArrayList<String> done)
    {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.done = done;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.itemTitle);
        ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.itemDelete);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.doneCheck);

        // check if the check boxes should be checked, if so check them and strike out the text
        checkBox.setChecked(done.get(position).equals("true"));
        if(checkBox.isChecked()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // debug
        Log.d("CustomArrayAdapter", "Boxes set to checked, first box: " + done.get(0));


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox check = (CheckBox) v;
                // checkboxes was checked or unchecked, update "done" accordingly
                done.set(position,check.isChecked() ? "true" : "false");
                notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // remove the item completely from BOTH values and done
                values.remove(position);
                done.remove(position);
                notifyDataSetChanged();
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final EditText edittext = new EditText(getContext());
                builder.setTitle("");
                builder.setMessage("Edit your item:");
                edittext.setText(values.get(position));
                edittext.setSelectAllOnFocus(true);

                builder.setPositiveButton("Save Item", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        values.set(position, edittext.getText().toString());
                        notifyDataSetChanged();
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
                return true;
            }
        });
        textView.setText(values.get(position));

        return rowView;
    }
}
