package com.example.nick.randomize;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nick on 6/2/2015.
 */
public class List implements Parcelable, Serializable {
    // title of list
    public String title;
    // items in list
    public ArrayList<String> listItems;
    // keys for above variables for Bundle
    private static final String KEY_TITLE = "title";
    private static final String KEY_LIST_ITEMS = "listItems";
    // random number generator for randomizing functions
    private Random listRand;
    private static File location;

    // Constructor
    public List(String titleIn) {
        this.title = titleIn;
        this.listRand = new Random(System.currentTimeMillis());
        this.listItems = new ArrayList<String>();
        location = null;
    }

    public List(String titleIn, ArrayList<String> itemsIn)
    {
        this.title = titleIn;
        this.listItems = itemsIn;
        location = null;
    }

    // Parcelable implementations
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_TITLE, title);
        bundle.putStringArrayList(KEY_LIST_ITEMS, listItems);

        out.writeBundle(bundle);
    }

    public static final Parcelable.Creator<List> CREATOR = new Creator<List>() {
        @Override
        public List createFromParcel(Parcel source) {
            // read bundle
            Bundle bundle = source.readBundle();

            // instantiate List
            return new List(bundle.getString(KEY_TITLE),bundle.getStringArrayList(KEY_LIST_ITEMS));
        }

        @Override
    public List[] newArray(int size)
        {
            return new List[size];
        }
    };

    // toString override for ListView
    @Override
    public String toString() {
        return title;
    }

    // psuedorandom function for each List
    // returns String value at randomized index
    public String getRandom() {
        return listItems.get(listRand.nextInt(listItems.size() - 1));
    }


    // TODO implement random ordering function


    // remove item from List at given index
    // wrapper for ArrayList
    public void removeItem(int index) {
        listItems.remove(index);
    }

    // add item to List
    // wrapper for ArrayList
    public void addItem(String item) {
        listItems.add(item);
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof List) {
            List list = (List) object;
            if (listItems == null) {
                return (list.listItems == null);
            } else {
                return listItems.equals(list.listItems) && title == list.title;
            }
        }

        return false;
    }

    /* commenting out the following to try simpler data storage
    // functions for reading/writing lists from/to storage
    public void saveData(Context context) {
        if (location == null)
            location = context.getExternalFilesDir(null);
        ObjectOutput out;
        try {
            File output = new File(location, "lists.data");
            out = new ObjectOutputStream(new FileOutputStream(output));
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<List> loadData(Context context) {
        if (location == null) {
            location = context.getExternalFilesDir(null);
        }
        ObjectInput in;
        ArrayList<List> loadList = null;
        try {
            FileInputStream fis = new FileInputStream(location.getPath() + File.separator + "lists.data");
            in = new ObjectInputStream(fis);
            //loadList = (ArrayList<List>) in.readObject();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadList;
    }

    */
}
