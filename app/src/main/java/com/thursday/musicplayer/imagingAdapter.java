package com.thursday.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import static com.thursday.musicplayer.R.drawable.design_ic_visibility;
import static com.thursday.musicplayer.R.drawable.ic_baseline_play_arrow_24;

public class imagingAdapter extends RecyclerView.Adapter<imagingAdapter.ViewHolder> {
    List<String> locallist;
    List<String> Imagelist;
    String album_id;
    private final AdapterView.OnItemClickListener onItemClickListener;
    Context context;

    public imagingAdapter(Context contest, AdapterView.OnItemClickListener onItemClickListener, List<String> values, List<String> imagelist) {
        locallist = values;
        Imagelist = imagelist;
        this.onItemClickListener = onItemClickListener;
        context = contest;
    }

    public Uri geturi(int position) {
        album_id = Imagelist.get(position);
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(album_id));

        return uri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_for_album_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextview().setText(locallist.get(position));
        Glide.with(context)
                .asBitmap()
                .placeholder(R.mipmap.icon_dtour)
                .load(geturi(position))
                .error(R.mipmap.icon_dtour)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return locallist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textview = null;
        private ImageView imageView = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.textView6);
            imageView = itemView.findViewById(R.id.imageView6);
            itemView.setOnClickListener(this);
        }

        public TextView getTextview() {
            return textview;
        }

        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());

        }
    }
}
