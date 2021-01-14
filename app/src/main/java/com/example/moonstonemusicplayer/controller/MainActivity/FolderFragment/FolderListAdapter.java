package com.example.moonstonemusicplayer.controller.MainActivity.FolderFragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

  private List<Object> folderSongList;
  Context context;
  LayoutInflater layoutInflater;

  public FolderListAdapter(@NonNull Context context, List<Object> folderSongList) {
    super(context, R.layout.folder_playlist_row_layout,folderSongList);
    this.folderSongList = folderSongList;
    this.context = context;
    this.layoutInflater = layoutInflater.from(context);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View rowView = convertView;

    Song aktuellerSong = null; Folder aktuellerFolder = null;
    if(folderSongList.get(position) instanceof Song){
      if(convertView == null){
        rowView = layoutInflater.inflate(R.layout.mainact_song_row_layout, parent, false);
      }
      aktuellerSong = ((Song) folderSongList.get(position));

      //init the views of songRowView
      TextView tv_name_song_main = rowView.findViewById(R.id.tv_name_song_main);
      TextView tv_artist_song_main = rowView.findViewById(R.id.tv_artist_song_main);
      TextView tv_duration_song_main = rowView.findViewById(R.id.main_tv_duration_song);

      tv_name_song_main.setText(aktuellerSong.getName());
      tv_artist_song_main.setText(aktuellerSong.getArtist());
      if(aktuellerSong.getArtist().isEmpty())tv_artist_song_main.setText("unknown artist");
      tv_duration_song_main.setText(aktuellerSong.getDurationString());

    } else if(folderSongList.get(position) instanceof Folder){
      if(convertView == null){
        rowView = layoutInflater.inflate(R.layout.folder_playlist_row_layout, parent, false);
      }
      aktuellerFolder = ((Folder) folderSongList.get(position));

      //init the views of songRowView
      TextView tv_folderSongItem = rowView.findViewById(R.id.tv_item);
      ImageView iv_folderSongItem = rowView.findViewById(R.id.iv_folder_playlist);
      tv_folderSongItem.setTextColor(context.getResources().getColor(R.color.colorPrimary));
      iv_folderSongItem.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
      ImageViewCompat.setImageTintList(iv_folderSongItem, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));

      if(aktuellerFolder != null){
        iv_folderSongItem.setBackground(context.getDrawable(R.drawable.ic_folder));
        tv_folderSongItem.setText(aktuellerFolder.getName());
      }
    } else {return rowView;}

    return rowView;
  }

}
