package com.gr7.photos;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.TextView;

public class ExifInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exif_info);

        String filepath = getIntent().getStringExtra("path");
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
            StringBuilder builder = new StringBuilder();

            builder.append("Date & Time: ");
            if (getExifTag(exif, ExifInterface.TAG_DATETIME).equals("")) {
                File file = new File(filepath);
                builder.append(new Date(file.lastModified()).toString() + "\n");
            } else {
                builder.append(getExifTag(exif, ExifInterface.TAG_DATETIME) + "\n");
            }

            builder.append("Focal Length: " + getExifTag(exif, ExifInterface.TAG_FOCAL_LENGTH) + "\n");
            builder.append("EXPOSURE: " + getExifTag(exif, ExifInterface.TAG_EXPOSURE_TIME) + "\n");
            builder.append("ISO: " + getExifTag(exif, ExifInterface.TAG_ISO) + "\n");
            builder.append("F-Stop: " + getExifTag(exif, ExifInterface.TAG_APERTURE) + "\n");
            builder.append("Image Length: " + getExifTag(exif, ExifInterface.TAG_IMAGE_LENGTH) + "\n");
            builder.append("Image Width: " + getExifTag(exif, ExifInterface.TAG_IMAGE_WIDTH) + "\n");
            builder.append("Camera Make: " + getExifTag(exif, ExifInterface.TAG_MAKE) + "\n");
            builder.append("Camera Model: " + getExifTag(exif, ExifInterface.TAG_MODEL) + "\n");
            builder.append("Camera White Balance: " + getExifTag(exif, ExifInterface.TAG_WHITE_BALANCE) + "\n");

            TextView info = (TextView) findViewById(R.id.exifinfo);
            info.setText(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle(filepath);
    }

    private String getExifTag(ExifInterface exif, String tag) {
        String attribute = exif.getAttribute(tag);

        return (null != attribute ? attribute : "");
    }
}