package com.example.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class ProblemDetail implements Serializable {

    private String trackingId;
    private String xoo_boo_lpn;
    private String quantity;
    private String processeAt;
    private String markedUnder;
    private String newId;
    private String comments;
    private long lastUpdatedTime;

    public ProblemDetail() {

    }

    public ProblemDetail(String trackingId, String xoo_boo_lpn, String quantity, String processeAt, String markedUnder, String newId, String comments, long lastUpdatedTime) {
        this.trackingId = trackingId;
        this.xoo_boo_lpn = xoo_boo_lpn;
        this.quantity = quantity;
        this.processeAt = processeAt;
        this.markedUnder = markedUnder;
        this.newId = newId;
        this.comments = comments;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getXoo_boo_lpn() {
        return xoo_boo_lpn;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getProcesseAt() {
        return processeAt;
    }

    public String getMarkedUnder() {
        return markedUnder;
    }

    public String getNewId() {
        return newId;
    }

    public String getComments() {
        return comments;
    }

}
