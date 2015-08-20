package com.example.nick.randomize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nick on 8/19/15.
 */
public class CustomArrayAdapterMain extends ArrayAdapter<RandomizeList> {
    private final Context context;
    private final ArrayList<RandomizeList> values;

    public CustomArrayAdapterMain(Context context, ArrayList<RandomizeList> values)
    {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_main, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.itemTitle);
        ImageButton randomizeButton = (ImageButton) rowView.findViewById(R.id.itemRandomize);

        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), EditList.class);
                intent.putExtra(MainActivity.EXTRA_CHOSEN, position); // position in array
                intent.putExtra(MainActivity.EXTRA_LISTS, values); // all RandomizeLists in array
                getContext().startActivity(intent); // send to EditList for editing
            }
        });
        randomizeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), RandomizeActivity.class);
                intent.putExtra(MainActivity.EXTRA_CHOSEN, position); // position in array
                intent.putExtra(MainActivity.EXTRA_LISTS, values); // all RandomizeLists in array
                getContext().startActivity(intent); // send to EditList for editing
            }
        });

        textView.setText(values.get(position).title);

        return rowView;
    }
}
