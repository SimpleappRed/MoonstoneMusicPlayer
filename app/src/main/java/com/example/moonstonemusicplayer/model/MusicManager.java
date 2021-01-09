package com.example.moonstonemusicplayer.model;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Holds all Songs available and manages them.
 * Is only used by the MainActivity.
 */
public class MusicManager {
    private Context context;
    private DataSource dataSource;

    private List<Song> playList; //the songs to be played
    private List<Song> displayedSongList = new ArrayList<>(); //the songs to be displayed by

    public MusicManager(Context baseContext) {
        this.context = baseContext;
        dataSource = new DataSource(this.context);
        playList = dataSource.getAllSong(60000);
        displayedSongList.addAll(playList);

    }

    /** loads local music and adds it to dataSource*/
    public void loadLocalMusic(File[] externalFileDirs){
        deleteAllSongs();//TODO: dont delete db but only local files
        dataSource.insertSongList(LocalSongLoader.findAllAudioFiles(externalFileDirs));
        playList.addAll(dataSource.getAllSong(60000));
        displayedSongList.addAll(playList);
    }

    public List<Song> getPlayList(){
        return this.playList;
    }

    public List<Song> getDisplayedSongList(){return this.displayedSongList;}

    public void searchSong(String searchterm){
        displayedSongList.clear();
        displayedSongList.addAll(dataSource.searchSongs(searchterm));
    }

    public void sortByTitle(){
        displayedSongList.clear();
        displayedSongList.addAll(dataSource.sortBy(DBHelperLocalSongs.COLUMN_TITLE,"ASC"));
    }

    public void sortByArtist(){
        displayedSongList.clear();
        displayedSongList.addAll(dataSource.sortBy(DBHelperLocalSongs.COLUMN_ARTIST,"ASC"));
    }

    public void sortByGenre(){
        displayedSongList.clear();
        displayedSongList.addAll(dataSource.sortBy(DBHelperLocalSongs.COLUMN_GENRE,"ASC"));
    }
    public void reverseList(){
        Collections.reverse(displayedSongList);
    }

    public void getAllSongs(){
        displayedSongList.clear();
        displayedSongList = dataSource.getAllSong(60000);
    }


    public void deleteAllSongs(){
        dataSource.deleteAllSongs();
        playList.clear();
        displayedSongList.clear();
    }


}