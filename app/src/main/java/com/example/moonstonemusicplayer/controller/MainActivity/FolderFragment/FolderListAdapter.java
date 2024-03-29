package com.example.moonstonemusicplayer.controller.MainActivity.FolderFragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.moonstonemusicplayer.R;
import com.example.moonstonemusicplayer.model.MainActivity.FolderFragment.Folder;
import com.example.moonstonemusicplayer.model.PlayListActivity.Song;

import java.util.List;

public class FolderListAdapter extends ArrayAdapter<Object> {

  private final List<Object> folderSongList;
  private final Context context;
  private final LayoutInflater layoutInflater;

  public FolderListAdapter(@NonNull Context context, List<Object> folderSongList) {
    super(context, R.layout.item_row_layout,folderSongList);
    this.folderSongList = folderSongList;
    this.context = context;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View rowView;
    if(convertView != null){
      rowView = convertView;
    } else {
      rowView = layoutInflater.inflate(R.layout.item_row_layout, parent, false);
    }

    Song currentSong = null; Folder currentFolder = null;
    if(folderSongList.get(position) instanceof Song){
      currentSong = ((Song) folderSongList.get(position));
    } else if(folderSongList.get(position) instanceof Folder){
      currentFolder = ((Folder) folderSongList.get(position));
    } else {return rowView;}



    //init the views of songRowView
    TextView tv_folderSongItem = rowView.findViewById(R.id.tv_item_name);
    ImageView iv_folderSongItem = rowView.findViewById(R.id.iv_item);
    tv_folderSongItem.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    iv_folderSongItem.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    ImageViewCompat.setImageTintList(iv_folderSongItem, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));

    if(currentFolder != null){
      iv_folderSongItem.setBackground(context.getDrawable(R.drawable.ic_folder));
      tv_folderSongItem.setText(currentFolder.getName());
    } else {
      iv_folderSongItem.setBackground(context.getDrawable(R.drawable.ic_music));
      tv_folderSongItem.setText(currentSong.getName());
    }

    if(currentSong != null){
      LinearLayout ll_artist_genre = rowView.findViewById(R.id.ll_artist_genre);
      TextView tv_artist_song = rowView.findViewById(R.id.tv_item_artist);
      TextView tv_duration_song = rowView.findViewById(R.id.item_tv_duration);
      TextView tv_duration_genre = rowView.findViewById(R.id.tv_item_genre);

      ll_artist_genre.setVisibility(View.VISIBLE);
      tv_artist_song.setVisibility(View.VISIBLE);
      tv_duration_song.setVisibility(View.VISIBLE);
      tv_duration_genre.setVisibility(View.VISIBLE);

      tv_artist_song.setText(currentSong.getArtist());
      if(currentSong.getArtist().isEmpty())tv_artist_song.setText("unknown artist");
      tv_duration_genre.setText(currentSong.getGenre());
      tv_duration_song.setText(currentSong.getDurationString());
    }

    return rowView;
  }

}
