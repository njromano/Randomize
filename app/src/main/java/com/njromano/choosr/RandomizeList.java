package com.njromano.choosr;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nick on 6/2/2015.
 */
public class RandomizeList implements Parcelable, Serializable {
    // title of list
    public String title;
    // items in list
    public ArrayList<String> listItems;
    public ArrayList<String> itemsDone;

    // Why is itemsDone an ArrayList<String>?
    //      I wanted the list of done items to be as close as possible to the list of actual items.
    //      A better way to do it would be to make a new List-like class for booleans or a
    //      Parcelable Item class that can be passed within an ArrayList itself. Time has been my
    //      constraint in this endeavor, however.

    // random number generator for randomizing functions
    private Random listRand;

    // Constructors
    public RandomizeList(String titleIn) {
        this.title = titleIn;
        this.listItems = new ArrayList<String>();
        this.itemsDone = new ArrayList<String>();
    }

    public RandomizeList(String titleIn, ArrayList<String> itemsIn)
    {
        this.title = titleIn;
        this.listItems = itemsIn;
    }

    // constructor for parcelable
    public RandomizeList(Parcel parcel)
    {
        this.title = parcel.readString();
        this.listItems = parcel.readArrayList(null);
        this.itemsDone = parcel.readArrayList(null);
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
        out.writeString(title);
        out.writeList(listItems);
        out.writeList(itemsDone);
    }

    public static final Parcelable.Creator<RandomizeList> CREATOR = new Creator<RandomizeList>() {
        @Override
        public RandomizeList createFromParcel(Parcel source) {
            return new RandomizeList(source);
        }

        @Override
    public RandomizeList[] newArray(int size)
        {
            return new RandomizeList[size];
        }
    };

    // toString override for ListView
    @Override
    public String toString() {
        return title;
    }

    // psuedorandom function for each List
    // returns String value at randomized index
    public int getRandom() {
        boolean isDone;
        int randIndex;

        // iterate through the list randomly until we find one that isn't "done"
        do {
            this.listRand = new Random();
            randIndex = listRand.nextInt(listItems.size());
            isDone = (itemsDone.get(randIndex).equals("true"));
        }while(isDone);

        // Previous versions returned the string from listItems, however RandomizeList needed only
        // the index itself for simplicity in that class. (i.e. I did not feel like managing another
        // variable there)
        return randIndex;
    }

    // remove item from List at given index
    // wrapper for ArrayList
    // doesn't seem to be used, however I am keeping it to demonstrate how it should be done
    public void removeItem(int index) {
        listItems.remove(index);
        itemsDone.remove(index);
    }

    // add item to List
    // wrapper for ArrayList
    public void addItem(String item) {
        listItems.add(item);
        itemsDone.add("false");
    }

    // to be honest, I forgot why I even needed this. perhaps for an ArrayList method?
    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof RandomizeList) {
            RandomizeList list = (RandomizeList) object;
            if (listItems == null) {
                return (list.listItems == null);
            } else {
                return listItems.equals(list.listItems) && title == list.title;
            }
        }

        return false;
    }
}
