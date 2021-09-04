package com.thursday.musicplayer;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.thursday.musicplayer.R.drawable.ic_baseline_play_arrow_24;

public class Recycler_playlist_adapter extends RecyclerView.Adapter<Recycler_playlist_adapter.ViewHolder> {
    List<String> locallist;
    Button button;
     private final AdapterView.OnItemClickListener onItemClickListener;
    Context context;

    public Recycler_playlist_adapter(Context contest, AdapterView.OnItemClickListener onItemClickListener, List<String> values) {
        locallist = values;
        this.onItemClickListener = onItemClickListener;
        context = contest;
    }

    @NonNull
    @Override
    public Recycler_playlist_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_adapter, parent, false);
        return new Recycler_playlist_adapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextview().setText(locallist.get(position));
        Glide.with(context)
                .asBitmap()
                .placeholder(ic_baseline_play_arrow_24)
                .load(R.mipmap.icon_dtour)
                .error(ic_baseline_play_arrow_24)
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
            textview = itemView.findViewById(R.id.textView7);
            imageView = itemView.findViewById(R.id.imageView7);
            button = itemView.findViewById(R.id.tripledot);
            textview.setOnClickListener(this);
            imageView.setOnClickListener(this);
            button.setOnClickListener(this);
        }

        public TextView getTextview() {
            return textview;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());
        }
    }

}

