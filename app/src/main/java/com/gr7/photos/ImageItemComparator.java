package com.gr7.photos;

import java.util.Comparator;

/**
 * Created by sf on 14/05/2016.
 */
public class ImageItemComparator implements Comparator<ImageItem> {
    public int compare(ImageItem left, ImageItem right){
        return left.getCalendar().compareTo(right.getCalendar());
    }
}
