package com.example.ultils;


import android.util.Log;

import com.example.model.Valume;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValumeUntils {
    // Singleton instance
    private static ValumeUntils instance;

    // List to store Valume objects
    private ArrayList<Valume> valumeList;

    // Private constructor to prevent instantiation
    private ValumeUntils() {
        valumeList = new ArrayList<>();
    }

    // Method to get the single instance of ValumeUntils
    public static synchronized ValumeUntils getInstance() {
        if (instance == null) {
            instance = new ValumeUntils();
        }
        return instance;
    }

    // Method to add Valume objects to the list
    public void addValume(Valume valume) {
        valumeList.add(valume);
    }

    // Method to get the list of Valume objects
    public ArrayList<Valume> getValumeList() {
        return valumeList;
    }

    // Method to clear the list if needed
    public void clearValumeList() {
        valumeList.clear();
    }
}
