package com.gr7.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by root on 01/05/2016.
 */
public class ImageItem implements Serializable {
    private Bitmap image;
    private String path;
    private Calendar calendar;

    public ImageItem(Bitmap image, String path, Calendar calendar) {
        this.path = path;
        this.image = image;
        this.calendar = calendar;
    }

    public ImageItem(String path, Calendar date) {
        this.path = path;
        this.calendar = date;
    }

    public Bitmap getImage(){
        return this.image;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
