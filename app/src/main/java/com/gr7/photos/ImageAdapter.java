package com.gr7.photos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 21/04/2016.
 */

public class ImageAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList();

    static int curDate = 0;
    static int curMonth = 0;
    static int curYear = 0;

    public ImageAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
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

            holder.date = (TextView) row.findViewById(R.id.dateGroup);
            holder.image = (ImageView) row.findViewById(R.id.image);


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem item = data.get(position);
        holder.image.setImageBitmap(item.getImage());

        if(item.getCalendar()== null){
            System.out.println("Null date");
            holder.date.setText("");
            holder.date.setVisibility(View.VISIBLE);
        } else {
            int tmpDate = item.getCalendar().get(Calendar.DATE);
            int tmpMonth = item.getCalendar().get(Calendar.MONTH);
            int tmpYear = item.getCalendar().get(Calendar.YEAR);
            if(curDate!=tmpDate || curMonth!=tmpMonth || curYear!=tmpYear){
                tmpMonth++;
                holder.date.setText(tmpDate+"/"+tmpMonth+"/"+tmpYear);
                holder.date.setVisibility(View.VISIBLE);
                curDate = tmpDate;
                curMonth = tmpMonth;
                curYear = tmpYear;
            }else {
                holder.date.setVisibility(View.GONE);
            }
        }
        return row;
    }

    static class ViewHolder {
        ImageView image;
        TextView date;
    }
}
