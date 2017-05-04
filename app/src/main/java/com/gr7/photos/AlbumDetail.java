package com.gr7.photos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class AlbumDetail extends AppCompatActivity {
    private GridView gridViewAlbum;
    private ImageAdapter gridAdapterAlbum;
    private SQLiteDatabase database;
    private ArrayList<String> arrayPath = new ArrayList<String>();
    private ArrayList<ImageItem> arrayImage = new ArrayList<ImageItem>();

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public ImageAdapter getGridAdapterAlbum() {
        return gridAdapterAlbum;
    }

    public void setGridAdapterAlbum(ImageAdapter gridAdapterAlbum) {
        this.gridAdapterAlbum = gridAdapterAlbum;
    }

    public ArrayList<String> getArrayPath() {
        return arrayPath;
    }

    public ArrayList<ImageItem> getArrayImage() {
        return arrayImage;
    }

    public void setArrayImage(ArrayList<ImageItem> arrayImage) {
        this.arrayImage = arrayImage;
    }

    public void setArrayPath(ArrayList<String> arrayPath) {
        this.arrayPath = arrayPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = openOrCreateDatabase(
                "album.db",
                MODE_PRIVATE,
                null
        );
        Integer albumId = getIntent().getIntExtra("albumId", -1);
        System.out.println(albumId);

        gridViewAlbum = (GridView) findViewById(R.id.gridViewAlbum);
        arrayImage = getAlbumDetails(albumId.toString());
        gridAdapterAlbum = new ImageAdapter(this, R.layout.grid_item_layout, arrayImage);
        Collections.sort(arrayImage, new ImageItemComparator());
        for (ImageItem item : arrayImage) {
            arrayPath.add(item.getPath());
        }
        gridViewAlbum.setAdapter(gridAdapterAlbum);

        gridViewAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(arrayPath.get(position));
                if (!file.exists()) {
                    arrayPath.remove(position);
                    arrayImage.remove(position);
                    Toast.makeText(AlbumDetail.this, "File deleted!!! Updating UI", Toast.LENGTH_SHORT).show();
                    UpdateUI();
                } else {
                    //ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                    Intent intent = new Intent(AlbumDetail.this, DetailsActivity.class);
                    intent.putExtra("array", arrayPath);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
    }

    private ArrayList<ImageItem> getAlbumDetails(String albumid) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        Cursor c = database.query(
                "tblimages",
                null, // select column
                "images_albumid = ?", // where or selection
                new String[]{albumid},
                null,
                null,
                null);
        c.moveToFirst();

        while (c.isAfterLast() == false) {
            String path = c.getString(1);
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapProcess.decodeSampledBitmapFromFile(path, 85, 85);

                Calendar calendar = Calendar.getInstance();
                Date date = null;
                try {

                    ExifInterface exif = new ExifInterface(path);
                    String dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                    if (dateString == null)
                        throw new Exception();
                    //System.out.println(dateString);
                    try {
                        date = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    date = null;

                    long fileDate = file.lastModified();
                    date = new Date(fileDate);

                }

                if (date != null) {
                    calendar.setTime(date);
                    System.out.print(calendar.get(Calendar.DATE));
                    System.out.print(calendar.get(Calendar.MONTH) + 1);
                    System.out.println(calendar.get(Calendar.YEAR));
                } else {
                    calendar = null;
                    System.out.println("Null calendar");
                }
                imageItems.add(new ImageItem(bitmap, path, calendar));
            }
            c.moveToNext();
        }

        return imageItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortalbumdetail) {
            if (item.getTitle().equals("Sort date descending")) {
                Collections.reverse(arrayImage);
                Collections.reverse(arrayPath);
                item.setTitle("Sort date ascending");
                UpdateUI();
                return true;
            } else if (item.getTitle().equals("Sort date ascending")) {
                Collections.sort(arrayImage, new ImageItemComparator());
                for (ImageItem i : arrayImage) {
                    arrayPath.add(i.getPath());
                }
                item.setTitle("Sort date descending");
                UpdateUI();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdateUI() {
        gridAdapterAlbum.notifyDataSetChanged();
    }
}
