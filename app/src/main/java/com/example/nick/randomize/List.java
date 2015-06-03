package com.example.nick.randomize;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nick on 6/2/2015.
 */
public class List {
    // title of list
    public String title;
    // items in list
    private ArrayList<String> listItems;
    // location of list's file
    // random number generator for randomizing functions
    private Random listRand;
    private static File location;

    // Constructor
    public List(String titleIn) {
        this.title = titleIn;
        this.listRand = new Random(System.currentTimeMillis());
        this.listItems = null;
        location = null;
    }


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
}
