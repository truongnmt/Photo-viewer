package com.gr7.photos;

/**
 * Created by root on 01/05/2016.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private int position = 0;
    private ArrayList<String> arrPath;
    private ImageView imageView;
    private int currentZoomLevel = 0;
    private float currentRotation = 0;
    private float originScaleX = 0;
    private float originScaleY = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;

    }

    public ArrayList<String> getArrPath() {
        return arrPath;
    }

    public void setArrPath(ArrayList<String> arrPath) {
        this.arrPath = arrPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
//        String title = getIntent().getStringExtra("title");
//        Bitmap bitmap = getIntent().getParcelableExtra("image");
        setArrPath((ArrayList<String>) getIntent().getSerializableExtra("array"));
        setPosition(getIntent().getIntExtra("position", 0));
        final TextView titleTextView = (TextView) findViewById(R.id.title);

        String filenameWithPath = getArrPath().get(getPosition());
        String[] tokens = filenameWithPath.split("[\\\\|/]");
        String filename = tokens[tokens.length - 1];
        titleTextView.setText(filename);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(BitmapProcess.decodeSampledBitmapFromFile(arrPath.get(position), 1080, 1920));
        originScaleX = imageView.getScaleX();
        originScaleY = imageView.getScaleY();

        ImageView zoomin = (ImageView) findViewById(R.id.zoomin);
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentZoomLevel < 3){
                    currentZoomLevel++;
                    float x = imageView.getScaleX();
                    float y = imageView.getScaleY();
                    imageView.setScaleX((float) (x+1));
                    imageView.setScaleY((float) (y+1));
                } else {
                    Toast.makeText(DetailsActivity.this, "Max zoom!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView zoomout = (ImageView) findViewById(R.id.zoomout);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentZoomLevel > 0){
                    currentZoomLevel--;
                    float x = imageView.getScaleX();
                    float y = imageView.getScaleY();
                    imageView.setScaleX((float) (x-1));
                    imageView.setScaleY((float) (y-1));
                } else {
                    Toast.makeText(DetailsActivity.this, "Normal image!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView rl = (ImageView) findViewById(R.id.rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rl:
//                        Matrix matrix = new Matrix();
//                        imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
//                        matrix.postRotate((float) 90f, imageView.getDrawable().getBounds().width() / 2, imageView.getDrawable().getBounds().height() / 2);
//                        imageView.setImageMatrix(matrix);
//
                        currentRotation -= 90;
                        imageView.setRotation(currentRotation);
                        break;
                }
            }

        });

        ImageView rr = (ImageView) findViewById(R.id.rr);
        rr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rr:
//                        Matrix matrix = new Matrix();
//                        imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
//                        matrix.postRotate((float) -90f, imageView.getDrawable().getBounds().width() / 2, imageView.getDrawable().getBounds().height() / 2);
//                        imageView.setImageMatrix(matrix);

                        currentRotation += 90;
                        imageView.setRotation(currentRotation);
                        break;
                }
            }

        });

        ImageView del = (ImageView) findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                File file = new File(getArrPath().get(getPosition()));
                                file.delete();
                                getArrPath().remove(getPosition());
                                if(getArrPath().size() == 0){
                                    finish();
                                    Toast.makeText(DetailsActivity.this, "Empty!!!", Toast.LENGTH_SHORT).show();
                                } else {

                                    if (getPosition() == arrPath.size() && arrPath.size() >= 1)
                                        setPosition(getPosition()-1);
                                    imageView.setImageBitmap(BitmapProcess.decodeSampledBitmapFromFile(getArrPath().get(position), 1080, 1920));
                                    Toast.makeText(DetailsActivity.this, "File deleted!!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, ExifInfoActivity.class);
                intent.putExtra("path", getArrPath().get(getPosition()));
                startActivity(intent);
            }
        });

        imageView.setOnTouchListener(new OnSwipeTouchListener(DetailsActivity.this) {
            public void onSwipeTop() {
//                info.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(DetailsActivity.this,ExifInfoActivity.class);
//                        intent.putExtra("path", arrPath.get(position));
//                        startActivity(intent);
//                    }
//                });
                if (currentRotation == 0 && currentZoomLevel == 0) {
//                    Toast.makeText(DetailsActivity.this, "top", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailsActivity.this, "Reset image before leave!!!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onSwipeRight() {
                if (currentRotation == 0 && currentZoomLevel == 0) {
                    if (getPosition() > 0) {
                        setPosition(getPosition()-1);
                        imageView.setImageBitmap(BitmapProcess.decodeSampledBitmapFromFile(getArrPath().get(getPosition()), 1000, 1000));
                        String filenameWithPath = getArrPath().get(getPosition());
                        String[] tokens = filenameWithPath.split("[\\\\|/]");
                        String filename = tokens[tokens.length - 1];
                        titleTextView.setText(filename);
                    } else {
                        Toast.makeText(DetailsActivity.this, "Reach begin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Reset image before leave!!!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onSwipeLeft() {
                if (currentRotation == 0 && currentZoomLevel == 0) {
                    if (getPosition() < arrPath.size() - 1) {
                        position++;
                        imageView.setImageBitmap(BitmapProcess.decodeSampledBitmapFromFile(arrPath.get(position), 1000, 1000));
                        String filenameWithPath = arrPath.get(position);
                        String[] tokens = filenameWithPath.split("[\\\\|/]");
                        String filename = tokens[tokens.length - 1];
                        titleTextView.setText(filename);
                    } else {
                        Toast.makeText(DetailsActivity.this, "Reach end", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Reset image before leave!!!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onSwipeBottom() {
                if (currentRotation == 0 && currentZoomLevel == 0) {
//                    Toast.makeText(DetailsActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailsActivity.this, "Reset image before leave!!!", Toast.LENGTH_SHORT).show();
                }
            }

        }) ;

        ImageView reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetNormalImageView();
            }
        });
    }

    public void SetNormalImageView(){
        imageView.setScaleX(originScaleX);
        imageView.setScaleY(originScaleY);
        currentZoomLevel = 0;

        currentRotation = 0;
        imageView.setRotation(currentRotation);
        Toast.makeText(DetailsActivity.this, "Reseted!!!", Toast.LENGTH_SHORT).show();
    }

}
