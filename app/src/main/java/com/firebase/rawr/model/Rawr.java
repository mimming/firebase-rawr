package com.firebase.rawr.model;

public class Rawr {
    private String from;
    private String when;

    public Rawr() {
    }

    public Rawr(String from, String when) {
        this.from = from;
        this.when = when;
    }

    public String getFrom() {
        return from;
    }

    public String getWhen() {
        return when;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setWhen(String when) {
        this.when = when;
    }
}
