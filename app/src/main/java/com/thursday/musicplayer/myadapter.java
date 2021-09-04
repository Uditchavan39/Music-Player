package com.thursday.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

class myAdapter extends ArrayAdapter<String> {


    public myAdapter(Context context, List<String> values) {
        super(context, R.layout.list_single, values);
    }

    static class ViewHolder {

        TextView textView;

        ImageView imageview;

    }

    public String tvShows = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tvShows = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_single, parent, false);
            tvShows = getItem(position);
            viewHolder.textView = convertView.findViewById(R.id.textView1);
            viewHolder.imageview = convertView.findViewById(R.id.imageView1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(tvShows);
        Glide.with(getContext())
                .asBitmap()
                .placeholder(R.mipmap.icon_dtour)
                .load(geturi(position))
                .error(R.mipmap.icon_dtour)
                .into(viewHolder.imageview);
        return convertView;
    }

    public Uri geturi(int position) {
        position = MainActivity.ListElementsArrayList.indexOf(tvShows);
        String album_id = MainActivity.ImageSongPathList.get(position);
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(album_id));
        return uri;
    }


}

