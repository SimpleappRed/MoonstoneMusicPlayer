package com.example.moonstonemusicplayer.model.Database.Playlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.moonstonemusicplayer.model.Database.Folder.DBFolder;
import com.example.moonstonemusicplayer.model.MainActivity.GenreFragment.Genre;
import com.example.moonstonemusicplayer.model.MainActivity.PlayListFragment.Playlist;
import com.example.moonstonemusicplayer.model.PlayListActivity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBPlaylists {
    //favorites is just another playlist
    private static final String FAVORITES_PLAYLIST_NAME = "MOONSTONE FAVORITES";

    private static final String TAG = DBPlaylists.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static DBPlaylists instance;

    //Variablendeklaration
    private final DBHelperPlaylists DBHelperPlaylists;
    private static SQLiteDatabase database_playlists;


    private final String[] COLUMNS = {
            com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_ID,
            com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME,
            com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_SONG_PATH
    };


    private DBPlaylists(Context context){
        Log.d(TAG,"Unsere DataSource erzeugt den DBHelperPlaylists");
        DBHelperPlaylists = new DBHelperPlaylists(context);
    }

    private void open_writable(){
        Log.d(TAG, "Eine schreibende Referenz auf die DB wird jetzt angefragt.");
        database_playlists = DBHelperPlaylists.getWritableDatabase();
        Log.d(TAG, "Datenbank-Referenz erhalten, Pfad zur Datenbank: "+ database_playlists.getPath());
    }

    private void open_readable(){
        Log.d(TAG, "Eine lesende Referenz auf die DB wird jetzt angefragt.");
        database_playlists = DBHelperPlaylists.getReadableDatabase();
        Log.d(TAG, "Datenbank-Referenz erhalten, Pfad zur Datenbank: "+ database_playlists.getPath());
    }

    private void close_db(){
        Log.d(TAG, "DB mit hilfe des DBHelperLocalSongss schließen");
        DBHelperPlaylists.close();
    }


    public String[] getAllPlaylistNames() {
        Set<String> allPlaylistNames = new HashSet<>();
        String query = "SELECT "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME +" FROM " + com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS;

        open_readable();
        //Zeiger auf die Einträge der Tabelle
        Cursor cursor = database_playlists.rawQuery(query, null);
        //Wenn Cursor beim ersten Eintrag steht
        if (cursor.moveToNext()) {
            do {
                allPlaylistNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close_db();
        return allPlaylistNames.toArray(new String[allPlaylistNames.size()]);
    }


    public List<Song> getAllFavorites(Context context){
        if(DEBUG)Log.d(TAG,"load Favorites");
        String query = "SELECT * FROM "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS
            +" WHERE "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " = '" +FAVORITES_PLAYLIST_NAME+ "'";
        return getSongListFromQuery(context, query);
    }

    public void deleteFromPlaylist(Song song, String playlistname){
        //öffnen der DB
        open_writable();

        //Song-Objekt aus Playlist DB löschen
        database_playlists.delete(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS,
            com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " = '" +playlistname+ "' AND " +
                    com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_SONG_PATH + " = '" +song.getPath()+ "'",null);

        //datenbank schließen und rückgabe des Songobjekts
        close_db();
    }

    public Song addToPlaylist(Context context, Song inputSong, String playlistname){
        if(DEBUG)Log.d(TAG,"add "+inputSong.getName()+" to playlist "+playlistname);

        //check if song is already in playlist
        String query = "SELECT * FROM "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS +" WHERE "+
                com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " LIKE '" + escapeString(playlistname)+ "' AND " +
                com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_SONG_PATH + " LIKE '" + escapeString(inputSong.getPath())+ "'";

        if(noResultsFromQuery(query)){
            //Anlegen von Wertepaaren zur Übergabe in Insert-Methode
            ContentValues values = new ContentValues();
            values.put(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME, playlistname);
            values.put(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_SONG_PATH, inputSong.getPath());

            //öffnen der DB
            open_writable();

            //Song-Objekt in DB einfügen und ID zurückbekommen
            long insertID = database_playlists.insert(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS, null, values);
            Log.d(TAG,"add to playlist: "+inputSong.getName()+" "+insertID);
            //Zeiger auf gerade eingefügtes Element
            Cursor cursor = database_playlists.query(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS,
                COLUMNS,
                com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_ID + " = " + insertID,
                null, null, null, null);

            //Zeiger auf Anfang bringen
            cursor.moveToFirst();

            //current Element auslesen
            Song current = cursorToSong(context,cursor);

            //zeiger zerstören
            cursor.close();

            //datenbank schließen und rückgabe des Songobjekts
            close_db();
            return current;
        }
        return inputSong;
    }

    public void addToFavorites(Context context,Song song){
       addToPlaylist(context,song,FAVORITES_PLAYLIST_NAME);
    }

    public void deleteFromFavorites(Song song){
        deleteFromPlaylist(song,FAVORITES_PLAYLIST_NAME);
    }

    public void deletePlaylist(Playlist playlist){
        open_writable();
        database_playlists.delete(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS,
                com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " = '" +playlist.getName()+ "'"
                ,null);
        close_db();
    }

    private List<Song> searchPlaylist(String searchterm){
        String query = "SELECT * FROM "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS +" WHERE ("+
                com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " LIKE '" +"%"+ escapeString(searchterm)+"%)";
        return null;
    }

    private boolean noResultsFromQuery(String query){
        List<Song> SongList = new ArrayList<>();
        open_readable();
        //Zeiger auf die Einträge der Tabelle
        Cursor cursor = database_playlists.rawQuery(query, null);
        boolean result = cursor.getCount() == 0;
        cursor.close();
        close_db();
        return result;
    }

    private List<Song> getSongListFromQuery(Context context, String query) {
        List<Song> SongList = new ArrayList<>();

        open_readable();
        //Zeiger auf die Einträge der Tabelle
        Cursor cursor = database_playlists.rawQuery(query, null);
        //Wenn Cursor beim ersten Eintrag steht
        if (cursor.moveToNext()) {
            do {
                int index = cursor.getInt(0);
                String playlistName = cursor.getString(1);
                String songURL = cursor.getString(2);

                if(!new File(songURL).exists()){
                    if(DEBUG)Log.d(TAG,"file does not exist: "+songURL);
                    continue;
                }

                Song song = DBFolder.getInstance(context).getSongFromPath(songURL);

                if(song != null)SongList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close_db();

        return SongList;
    }

    public List<Playlist> getAllPlaylists(Context context) {
        List<Playlist> allPlaylists = new ArrayList<>();
        String[] allPlaylistNames = getAllPlaylistNames();
        Log.d(TAG, Arrays.toString(allPlaylistNames));
        for(String playlistName: allPlaylistNames){
            String query = "SELECT * FROM "+ com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.TABLE_PLAYLISTS +" WHERE "+
                    com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_PLAYLIST_NAME + " LIKE '" + escapeString(playlistName)+ "'";
            List<Song> playlistSongs = getSongListFromQuery(context,query);
            allPlaylists.add(new Playlist(playlistName,playlistSongs));
        }
        return allPlaylists;
    }


    private Song cursorToSong(Context context, Cursor cursor) {
        //get Indexes
        int idPath = cursor.getColumnIndex(com.example.moonstonemusicplayer.model.Database.Playlist.DBHelperPlaylists.COLUMN_SONG_PATH);

        //create Song from values
        return DBFolder.getInstance(context).getSongFromPath(cursor.getString(idPath));
    }

    public static DBPlaylists getInstance(Context context){
        if(instance == null){
            instance = new DBPlaylists(context);
        }
        return instance;
    }


    public static String escapeString(String query){
        query = query.replace("'","''");
        return query;
    }


}
