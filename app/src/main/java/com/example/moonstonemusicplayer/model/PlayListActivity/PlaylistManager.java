package com.example.moonstonemusicplayer.model.PlayListActivity;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import com.example.moonstonemusicplayer.model.Database.DBSonglists;


/**
 * Holds all Songs available to PlaylistActivity.
 * Is only used by the MainActivity.
 */
public class PlaylistManager {
    private final Context context;


    private final List<Song> playList; //the songs to be played
    private List<Song> displayedSongList = new ArrayList<>(); //the songs to be displayed by

    public PlaylistManager(Context baseContext, Song[] playlist) {
        this.context = baseContext;

        playList = new ArrayList<>(Arrays.asList(playlist));
        displayedSongList = new ArrayList<>(Arrays.asList(playlist));
    }


    /** loads local music and adds it to DataSourceSingleton.getInstance(context)
    public void loadLocalMusic(){
        deleteAllSongs();//TODO: dont delete db but only local files
        File[] externalFileDirs = context.getExternalMediaDirs(); //getExternalMediaDirs actually does get both internal and external sdcards
        DataSourceSingleton.getInstance(context).insertSongList(LocalSongLoader.findAllAudioFilesInDir(externalFileDirs));
        playList.addAll(DataSourceSingleton.getInstance(context).getAllSong(60000));
        displayedSongList.clear();
        displayedSongList.addAll(playList);
    }*/

    public List<Song> getPlayList(){
        return this.playList;
    }

    public List<Song> getDisplayedSongList(){return this.displayedSongList;}


    public void searchSong(String searchterm){
        this.displayedSongList.clear();
        for(Song song: playList) {
            if (song.getName().toLowerCase().contains(searchterm.toLowerCase()))
                displayedSongList.add(song);
        }
        /*
        intersectPlaylist(DataSourceSingleton.getInstance(context).searchSongs(searchterm));
         */
        //displayedSongList.addAll(playList);
    }


    public void sortByTitle(){
        Collections.sort(displayedSongList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        /*
        intersectPlaylist(DataSourceSingleton.getInstance(context).sortBy(DBHelper.COLUMN_TITLE,"ASC"));
        displayedSongList.addAll(playList);
         */
    }

    public void sortByArtist(){
        Collections.sort(displayedSongList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return (o1.getArtist().compareTo(o2.getArtist()));
            }
        });
        /*
        intersectPlaylist(DataSourceSingleton.getInstance(context).sortBy(DBHelper.COLUMN_ARTIST,"ASC"));
        displayedSongList.addAll(playList);
         */
    }

    public void sortByGenre(){
        Collections.sort(displayedSongList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return (o1.getGenre().compareTo(o2.getGenre()));
            }
        });
        /*
        intersectPlaylist(DataSourceSingleton.getInstance(context).sortBy(DBHelper.COLUMN_GENRE,"ASC"));
        displayedSongList.addAll(playList);
         */
    }

    public void reverseList(){
        Collections.reverse(displayedSongList);
    }

    public void deleteAllSongs(){
        //DBSonglists.getInstance(context).deleteAllSongs();
        playList.clear();
        displayedSongList.clear();
    }

    public void sortByDuration() {
        Collections.sort(displayedSongList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return (int) (o1.getDuration_ms() - o2.getDuration_ms());
            }
        });
    }

    /*
    * public void intersectPlaylist(List<Song> input){
        input.retainAll(playList);
        playList.clear();
        playList.addAll(input);
    }
    * */

}
