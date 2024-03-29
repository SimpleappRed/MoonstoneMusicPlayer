package com.example.moonstonemusicplayer.model.MainActivity.PlayListFragment;

import android.content.Context;
import android.util.Log;

import com.example.moonstonemusicplayer.model.Database.Playlist.DBPlaylists;
//import com.example.moonstonemusicplayer.model.Database.DBSonglists;
import com.example.moonstonemusicplayer.model.PlayListActivity.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** saves and loads playlists and contains the current (displayed) playlist in playlistfragment*/
public class PlaylistListManager {

  private static final String TAG = PlaylistListManager.class.getSimpleName();
  private final Context context;
  //private DataSource dataSource;

  private Playlist currentPlaylist;
  private List<Playlist> playlists = new ArrayList<>();
  private final List<Playlist> playlists_backup = new ArrayList<>();

  public PlaylistListManager(Context baseContext) {
    this.context = baseContext;
    loadPlaylistsFromDB(baseContext);
  }

  /** loads local music and adds it to dataSource*/
  public void loadPlaylistsFromDB(Context context){
    if(context != null){
      playlists.clear();
      playlists_backup.clear();
      Log.d(TAG,"loadPlaylistsFromDB");
      this.playlists.addAll(DBPlaylists.getInstance(context).getAllPlaylists(context));
      this.playlists_backup.addAll(playlists);
    }
  }

  public Playlist getPlaylist(String name){
    for(Playlist playList: this.playlists){
      if(playList.name.equals(name))return playList;
    }
    return null;
  }

  public List<Playlist> getAllPlaylists(){
    playlists.clear();
    playlists.addAll(playlists_backup);
    return this.playlists;
  }

  public List<String> getPlaylistNames(){
    List<String> playlistNames = new ArrayList<>();
    for(Playlist playList: this.playlists){
      playlistNames.add(playList.name);
    }
    return playlistNames;
  }

  public Playlist getCurrentPlaylist() {
    return currentPlaylist;
  }

  public void setCurrentPlaylist(Playlist currentPlaylist) {
    this.currentPlaylist = currentPlaylist;
  }

  public List<Playlist> getPlaylists() {
    return playlists;
  }

  public void setPlaylists(List<Playlist> playlists) {
    this.playlists = playlists;
  }

  public Playlist[] getAllPlaylistsMatchingQuery(String query) {
    List<Playlist> result = new ArrayList<>();
    for(Playlist playlist: getAllPlaylists()){
      if(playlist.getName().toLowerCase().contains(query.toLowerCase())){
        result.add(playlist);
      }
    }
    return result.toArray(new Playlist[result.size()]);
  }

  public Song[] getAllSongsMatchingQuery(String query) {
    List<Song> result = new ArrayList<>();
    for(Playlist playlist: getAllPlaylists()){
      for(Song song: playlist.getPlaylist()) {
        if(song.getName().toLowerCase().contains(query.toLowerCase())){
          result.add(song);
        }
      }
    }
    return result.toArray(new Song[result.size()]);
  }



  public void deletePlaylist(Playlist playlist) {
    playlists_backup.remove(playlist);
    playlists.remove(playlist);
  }

  public void deleteFromPlaylist(Song song, String name) {
    for(Playlist playlist: playlists_backup){
      if(playlist.name.equals(name)){
        playlist.getPlaylist().remove(song);
      }
    }
    for(Playlist playlist: playlists){
      if(playlist.name.equals(name)){
        playlist.getPlaylist().remove(song);
      }
    }
  }

  public void sortSongsByGenre() {
    if(currentPlaylist != null){
      Collections.sort(currentPlaylist.getPlaylist(), new Comparator<Song>() {
        @Override
        public int compare(Song o1, Song o2) {
          return o1.getGenre().compareTo(o2.getGenre());
        }
      });
    }
  }

  public void sortSongsByDuration() {
    if(currentPlaylist != null){
      Collections.sort(currentPlaylist.getPlaylist(), new Comparator<Song>() {
        @Override
        public int compare(Song o1, Song o2) {
          return (int)(o1.getDuration_ms() - o2.getDuration_ms());
        }
      });
    }
  }

  public void sortSongsByArtist() {
    if(currentPlaylist != null){
      Collections.sort(currentPlaylist.getPlaylist(), new Comparator<Song>() {
        @Override
        public int compare(Song o1, Song o2) {
          return o1.getArtist().compareTo(o2.getArtist());
        }
      });
    }
  }

  public void sortSongsByName() {
    if(currentPlaylist != null){
      Collections.sort(currentPlaylist.getPlaylist(), new Comparator<Song>() {
        @Override
        public int compare(Song o1, Song o2) {
          return o1.getName().compareTo(o2.getName());
        }
      });
    }
    Collections.sort(playlists, new Comparator<Playlist>() {
      @Override
      public int compare(Playlist o1, Playlist o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
  }

  public void reverse(){
    if(currentPlaylist != null){
      Collections.reverse(currentPlaylist.getPlaylist());
    }
  }
}
