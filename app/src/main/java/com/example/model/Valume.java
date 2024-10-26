package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Valume {
    private String uri;
    private String name;
    private String size;
    private String duration;
    private String date;
    private int volumedb=0; // New field for storing volume level

    private boolean isPlaying = false; // Add this field to track playback state



    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }


    public Valume(String uri, String name, String duration) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getVolume() {
        return volumedb;
    }

    public void setVolume(int volumedb) {
        this.volumedb = volumedb;
    }

    public Valume(Parcel in) {
        uri = in.readString();
        name = in.readString();
        size = in.readString();
        duration = in.readString();
        date = in.readString();
        volumedb = in.readInt(); // Read volume from Parcel
    }


    @Override
    public String toString() {
        return "Valume{" +
                "uri='" + uri + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", duration='" + duration + '\'' +
                ", date='" + date + '\'' +
                ", volumedb=" + volumedb +
                '}';
    }
}
