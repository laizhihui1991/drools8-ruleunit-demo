package org.example;

public class Measurement {
    private String id;
    private String val;

    public Measurement(String id, String val) {
        this.id = id;
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public String getVal() {
        return val;
    }
}