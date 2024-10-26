package com.example.model;

public class Speed {
    private String uri;
    private String name;
    private String duration;
    private int speed=120; // Tốc độ hiện tại

    public Speed(String uri, String name, String duration) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    @Override
    public String toString() {
        return "speed{" +
                "uri='" + uri + '\'' +
                ", name='" + name + '\'' +

                ", duration='" + duration + '\'' +

                ", speed=" + speed +
                '}';
    }
}
