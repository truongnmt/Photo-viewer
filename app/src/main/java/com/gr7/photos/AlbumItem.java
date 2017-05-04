package com.gr7.photos;

import java.util.Calendar;

/**
 * Created by root on 08/05/2016.
 */
public class AlbumItem {
    private int albumId;
    private String title;
    private Calendar dateCreated;
    private int totalImage;
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlbumItem(int albumId, String title, Calendar dateCreated, int totalImage, String cover) {
        this.albumId = albumId;
        this.title = title;
        this.dateCreated = dateCreated;
        this.totalImage = totalImage;
        this.cover = cover;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }

    public int getAlbumId() {

        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
