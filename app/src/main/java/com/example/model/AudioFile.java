package com.example.model;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AudioFile implements Parcelable {
    private String uri;
    private String name;
    private String size;
    private String duration;
    private String date;
    public AudioFile(String uri, String name, String size, String duration, String date) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.date = date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setSize(String size) {
        this.size = size;
    }

    protected AudioFile(Parcel in) {
        uri = in.readString();
        name = in.readString();
        size = in.readString();
        duration = in.readString();
        date = in.readString();
    }

    public static final Creator<AudioFile> CREATOR = new Creator<AudioFile>() {
        @Override
        public AudioFile createFromParcel(Parcel in) {
            return new AudioFile(in);
        }

        @Override
        public AudioFile[] newArray(int size) {
            return new AudioFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(name);
        dest.writeString(size);
        dest.writeString(duration);
        dest.writeString(date);
    }

    // Getter methods for the audio file properties
    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }
}
