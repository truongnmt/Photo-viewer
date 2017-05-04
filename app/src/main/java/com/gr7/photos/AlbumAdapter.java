package com.gr7.photos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by root on 08/05/2016.
 */
public class AlbumAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<AlbumItem> data = new ArrayList();

    public AlbumAdapter(Context context, int layoutResourceId, ArrayList<AlbumItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.image = (ImageView) row.findViewById(R.id.albumImage);
            holder.text = (TextView) row.findViewById(R.id.albumText);

            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        AlbumItem item = data.get(position);
        String tmp;
        if(item.getDateCreated()== null){
            System.out.println("Unknown date created");
            tmp = item.getTitle()+ " - "
                    + "Date created: Unknown";// + " - Total images:"
                    //+ item.getTotalImage();
        } else {
            int tmpDate = item.getDateCreated().get(Calendar.DATE);
            int tmpMonth = item.getDateCreated().get(Calendar.MONTH) + 1;
            int tmpYear = item.getDateCreated().get(Calendar.YEAR);
            tmp = item.getTitle()+ " - "
                    + "Date created: " + tmpDate + "/" + tmpMonth + "/" + tmpYear;
                    //+ " - Total images:" + item.getTotalImage();
        }

        holder.text.setText(tmp);
        if(item.getCover()!=null){
            holder.image.setImageBitmap(BitmapProcess.decodeSampledBitmapFromFile(item.getCover(),150,150));
        }
        return row;
    }

    static class ViewHolder {
        ImageView image;
        TextView text;
    }
}
