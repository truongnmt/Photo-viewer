package com.gr7.photos;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AlbumActivity extends AppCompatActivity {
    private Button addAlbum;
    private int RESULT_LOAD_IMG = 1;
    private int RESULT_LOAD_IMG_ALBUM = 2;
    private SQLiteDatabase database;
    private ListView listView;
    private AlbumAdapter listAdapter;
    private String path = "Blank";
    long position = 0;
    private int albumIDedit;
    private ArrayList<AlbumItem> albumItem = new ArrayList<AlbumItem>();

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public AlbumAdapter getListAdapter() {
        return listAdapter;
    }

    public int getAlbumIDedit() {
        return albumIDedit;
    }

    public void setAlbumIDedit(int albumIDedit) {
        this.albumIDedit = albumIDedit;
    }

    public void setListAdapter(AlbumAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    public ArrayList<AlbumItem> getAlbumItem() {
        return albumItem;
    }

    public void setAlbumItem(ArrayList<AlbumItem> albumItem) {
        this.albumItem = albumItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createOrOpenDb();

        addAlbum = (Button) findViewById(R.id.addAlbum);
        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        albumItem = getData();
        listAdapter = new AlbumAdapter(this, R.layout.list_item_layout, albumItem);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumItem itemAlbum = (AlbumItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(AlbumActivity.this, AlbumDetail.class);
                intent.putExtra("albumId", itemAlbum.getAlbumId());
                startActivity(intent);
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_album, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = info.id;
        switch (item.getItemId()) {
            case R.id.edittitle:
                albumIDedit = albumItem.get((int) position).getAlbumId();
                AlertDialog.Builder alert = new AlertDialog.Builder(AlbumActivity.this);
                alert.setTitle("Title");
                alert.setMessage("Your album title:");
                final EditText input = new EditText(AlbumActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newTitle = "";
                        if (!(input.getText()).toString().equals(""))
                            newTitle = (input.getText()).toString();

                        String sql = "UPDATE tblalbum ";
                        sql += "SET album_title = \"" + newTitle;
                        sql += "\" where album_id = " + albumIDedit;
                        database.execSQL(sql);
                        updateUI();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AlbumActivity.this, "Title not change!", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
                break;

            case R.id.editalbum:
                albumIDedit = albumItem.get((int) position).getAlbumId();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG_ALBUM);

                break;

            case R.id.delete:
                albumIDedit = albumItem.get((int) position).getAlbumId();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                String sql = "DELETE FROM tblalbum ";
                                sql += "WHERE album_id = " + albumIDedit;
                                sql += "; DELETE FROM tblimage ";
                                sql += "WHERE images_albumid = " + albumIDedit;

                                database.execSQL(sql);
                                updateUI();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public ArrayList<AlbumItem> getData() {
        final ArrayList<AlbumItem> albumItem = new ArrayList<>();

        Cursor c = database.query("tblalbum", null, null, null, null, null, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            String dateString = c.getString(2);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(dateString));
            File file = new File(c.getString(4));
            if(!file.exists()){
                albumItem.add(new AlbumItem(c.getInt(0), c.getString(1), cal, c.getInt(3), null));
            } else {
                albumItem.add(new AlbumItem(c.getInt(0), c.getString(1), cal, c.getInt(3), c.getString(4)));
            }
            c.moveToNext();
        }
        return albumItem;
    }

    public void createOrOpenDb() {
        database = openOrCreateDatabase(
                "album.db",
                MODE_PRIVATE,
                null
        );

        String sql = "CREATE TABLE IF NOT EXISTS tblalbum (";
        sql += "album_id      INTEGER primary key AUTOINCREMENT,";
        sql += "album_title   TEXT NOT NULL,";
        sql += "album_date    TEXT,";
        sql += "album_total   INTEGER,";
        sql += "album_cover   TEXT)";
        database.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS  tblimages (";
        sql += "images_albumid      INTEGER REFERENCES tblalbum(album_id) ,";
        sql += "images_path   TEXT NOT NULL)";
        database.execSQL(sql);

    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            AlertDialog.Builder alert = new AlertDialog.Builder(AlbumActivity.this);
            alert.setTitle("Title");
            alert.setMessage("Your album title:");
            final EditText input = new EditText(AlbumActivity.this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (!(input.getText()).toString().equals("")){
                        path = (input.getText()).toString();
                    }else {
                        path = "Blank";
                    }
                    addAlbum(data);
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    path = "Blank";
                }
            });
            alert.show();
        } else if (requestCode == RESULT_LOAD_IMG_ALBUM && resultCode == RESULT_OK) {
            editAlbum(data);
        }
    }

    public void addAlbum(Intent data) {
        final ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        String s = String.valueOf(cal.getTimeInMillis());
        values.put("album_title", path);
        values.put("album_date", s);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        ArrayList<String> imagesEncodedList = new ArrayList<>();
        String imageEncoded;
        if (data.getClipData() != null) {
            values.put("album_total", data.getClipData().getItemCount());
            String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getClipData().getItemAt(0).getUri(), filePathColumn1, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn1[0]);
            values.put("album_cover", cursor.getString(columnIndex));
            long albumId = database.insert("tblalbum", null, values);
            if (albumId == -1) {
                Toast.makeText(this, "Failed to insert album", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "insert album done", Toast.LENGTH_LONG).show();
            }
            try {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        cursor.close();
                        ContentValues values2 = new ContentValues();
                        values2.put("images_albumid", albumId);
                        values2.put("images_path", imageEncoded);
                        if (database.insert("tblimages", null, values2) == -1) {
                            Toast.makeText(this, "Failed to insert images", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(this, "insert images done", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }
        } else if (data.getData() != null) {
            Uri selectedImage = data.getData();
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            cursor.moveToFirst();
            imageEncoded = cursor.getString(columnIndex);
            cursor.close();
            ContentValues values2 = new ContentValues();
            values.put("album_total", 1);
            values.put("album_cover", imageEncoded);
            values2.put("images_albumid", database.insert("tblalbum", null, values));
            values2.put("images_path", imageEncoded);

            if (database.insert("tblimages", null, values2) == -1) {
                Toast.makeText(this, "Failed to insert images", Toast.LENGTH_LONG).show();
            } else {
                //System.out.println("insert images done");
            }
        }
        updateUI();
    }

    public void editAlbum(Intent data) {
        final ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        String s = String.valueOf(cal.getTimeInMillis());
        values.put("album_title", path);
        values.put("album_date", s);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        ArrayList<String> imagesEncodedList = new ArrayList<>();
        String imageEncoded;
        int sizeAlbum = 0;

        String sql = "SELECT count(images_albumid) from tblimages where images_albumid = " + albumIDedit;
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            sizeAlbum = cursor.getInt(0);
        }

        if (data.getClipData() != null) {
            ClipData mClipData = data.getClipData();
            sizeAlbum += mClipData.getItemCount();
            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                mArrayUri.add(uri);
                cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);
                imagesEncodedList.add(imageEncoded);
                cursor.close();
                ContentValues values2 = new ContentValues();
                values2.put("images_albumid", albumIDedit);
                values2.put("images_path", imageEncoded);
                if (database.insert("tblimages", null, values2) == -1) {
                    Toast.makeText(this, "Failed to insert images", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(this, "insert images done", Toast.LENGTH_LONG).show();
                }
            }
            sql = "UPDATE tblalbum set album_total = " + sizeAlbum;
            sql += " where album_id = " + albumIDedit;
            database.execSQL(sql);

        } else if (data.getData() != null) {
            sizeAlbum++;
            Uri selectedImage = data.getData();
            cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            cursor.moveToFirst();
            imageEncoded = cursor.getString(columnIndex);
            cursor.close();
            ContentValues values2 = new ContentValues();
            values2.put("images_albumid", albumIDedit);
            values2.put("images_path", imageEncoded);

            sql = "UPDATE tblalbum ";
            sql += "SET album_cover = \"" + imageEncoded;

            sql += "\" where album_id = " + albumIDedit;

            database.execSQL(sql);

            if (database.insert("tblimages", null, values2) == -1) {
                Toast.makeText(this, "Failed to insert images", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "insert images done", Toast.LENGTH_LONG).show();
                System.out.println("insert images update album done");
            }
            sql = "UPDATE tblalbum set album_total = " + sizeAlbum;
            sql += " where album_id = " + albumIDedit;
            database.execSQL(sql);
        }
        updateUI();
    }

    public void updateUI() {
        albumItem = getData();
        listAdapter = new AlbumAdapter(this, R.layout.list_item_layout, albumItem);
        listView.setAdapter(listAdapter);
    }

}