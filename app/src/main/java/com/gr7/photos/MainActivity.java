package com.gr7.photos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private ArrayList<ImageItem> arrayImage = new ArrayList<ImageItem>();
    private ArrayList<String> arrPath = new ArrayList<String>();
    private GridView gridView;
    private ImageAdapter imageAdapter;

    public ArrayList<ImageItem> getArrayImage() {
        return arrayImage;
    }

    public void setArrayImage(ArrayList<ImageItem> arrayImage) {
        this.arrayImage = arrayImage;
    }

    public ArrayList<String> getArrPath() {
        return arrPath;
    }

    public void setArrPath(ArrayList<String> arrPath) {
        this.arrPath = arrPath;
    }

    public ImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    public void setImageAdapter(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public GridView getGridView() {
        return gridView;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    public boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    public ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(dataColumnIndex);
            File file = new File(path);
            if (!file.exists()) {
                break;
            }
            Calendar calendar = Calendar.getInstance();
            Date date = null;
            try {
                ExifInterface exif = new ExifInterface(path);
                String dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                if (dateString == null)
                    throw new Exception();
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
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
            Bitmap bitmap = BitmapProcess.decodeSampledBitmapFromFile(path, 85, 85);
            imageItems.add(new ImageItem(bitmap, path, calendar));
        }
        cursor.close();
        return imageItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!checkPermission()) {
            Toast.makeText(this, "Please grant permission!!!", Toast.LENGTH_SHORT).show();
            requirePermission();
        } else {
            loadActivity();
        }
    }

    public void loadActivity() {
        setGridView((GridView) findViewById(R.id.gridView));
        setArrayImage(getData());
        Collections.sort(getArrayImage(), new ImageItemComparator());
        for (ImageItem item : getArrayImage()) {
            getArrPath().add(item.getPath());
        }

        setImageAdapter(new ImageAdapter(this, R.layout.grid_item_layout, getArrayImage()));
        getGridView().setAdapter(getImageAdapter());

        getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(getArrPath().get(position));
                if (!file.exists()) {
                    getArrPath().remove(position);
                    getArrayImage().remove(position);
                    Toast.makeText(MainActivity.this, "File deleted!!! Updating UI", Toast.LENGTH_SHORT).show();
                    UpdateUI();
                } else {
                    //ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("array", getArrPath());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort) {
            if (item.getTitle().equals("Sort date descending")) {
                Collections.reverse(getArrayImage());
                Collections.reverse(getArrPath());
                item.setTitle("Sort date ascending");
                UpdateUI();
                return true;
            } else if (item.getTitle().equals("Sort date ascending")) {
                Collections.sort(getArrayImage(), new ImageItemComparator());
                item.setTitle("Sort date descending");
                UpdateUI();
                return true;
            }
        }

        if (id == R.id.album) {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Got permission", Toast.LENGTH_SHORT).show();
                    loadActivity();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly!!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void UpdateUI() {
        getImageAdapter().notifyDataSetChanged();
    }
}
