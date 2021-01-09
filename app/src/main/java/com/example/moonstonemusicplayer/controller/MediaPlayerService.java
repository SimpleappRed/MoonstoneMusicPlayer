package com.example.moonstonemusicplayer.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.moonstonemusicplayer.R;
import com.example.moonstonemusicplayer.model.PlayListModel;
import com.example.moonstonemusicplayer.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/** MediaPlayerService
 * Plays the List<Song> specified by MusicPlayer.
 */
public class MediaPlayerService extends Service
       implements MediaPlayer.OnCompletionListener,
                  MediaPlayer.OnPreparedListener,
                  MediaPlayer.OnErrorListener,
                  MediaPlayer.OnSeekCompleteListener,
                  MediaPlayer.OnInfoListener,
                  MediaPlayer.OnBufferingUpdateListener,
                  AudioManager.OnAudioFocusChangeListener {

  private static final String TAG = MediaPlayerService.class.getSimpleName();
  private static final boolean DEBUG = true;

  public static final String PLAYLISTEXTRA = "playlist";
  private final IBinder iBinder = new LocalBinder();;
  private MediaPlayer mediaPlayer;
  private boolean isMediaPlayerPrepared = false;
  private PlayListModel playListModel;

  private AudioManager audioManager;
  private int resumePosition = 0;

  public MediaPlayerService() {}


  /** inits mediaplayer and sets Listeners */
  private void initMediaPlayer(){
    onDestroy();
    if(DEBUG)Log.d(TAG,"initMediaPlayer");
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.setOnErrorListener(this);
    mediaPlayer.setOnPreparedListener(this);
    mediaPlayer.setOnSeekCompleteListener(this);
    mediaPlayer.setOnInfoListener(this);
    mediaPlayer.setOnBufferingUpdateListener(this);

    //setzte Mediaplayer zurück damit er nicht auf falsche AudioDatei verweist
    mediaPlayer.reset();
    //stelle wiedergabe lautstärke auf musik ein
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    try {
      //weise Mediendatei der Datenquelle zu
      mediaPlayer.setDataSource(playListModel.getCurrentSong().getURI());
    } catch (IOException e) {
      e.printStackTrace();
      stopSelf();
    }
    //bereitet MediaPlayer für Wiedergabe vor
    mediaPlayer.prepareAsync();
    resumePosition = 0;
  }

  //public interface
  public void resume() {
    if(DEBUG)Log.d(TAG,"resume: "+resumePosition);

    //setze Wiedergabe fort
    requestAudioFocus();
    if(mediaPlayer == null)initMediaPlayer();
    else {
      if(!isMediaPlayerPrepared){//state stop
        mediaPlayer.prepareAsync();
      } else {
        mediaPlayer.seekTo(resumePosition);
        mediaPlayer.setVolume(1.0f,1.0f);
        if(!mediaPlayer.isPlaying())mediaPlayer.start();
      }
    }

  }

  public void pause() {
    if(mediaPlayer != null && mediaPlayer.isPlaying()){
      mediaPlayer.pause();
      resumePosition = mediaPlayer.getCurrentPosition();
    }
  }

  public boolean mediaPlayerReady(){return (mediaPlayer != null && isPlayingMusic());}

  public boolean isPlayingMusic() {
    if(mediaPlayer != null) return mediaPlayer.isPlaying();
    else return false;
  }

  public int getCurrentPosition() {
    if(mediaPlayerReady())return mediaPlayer.getCurrentPosition();
    else return -1;
  }

  private void playMedia(){
    if(mediaPlayer != null && !mediaPlayer.isPlaying()){
      mediaPlayer.start();
    }
  }

  private void stopMedia(){
    if(mediaPlayer != null && mediaPlayer.isPlaying()){
      {mediaPlayer.stop(); isMediaPlayerPrepared=false;}
    }
  }

  //Listener interface
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    //initialize the playListModel with the playlist passed as List<Song>
    List<Song> playList = new ArrayList<>();
    int i = 0;
    while(true){
      String extraKey = PLAYLISTEXTRA+"_"+i;
      if(intent.hasExtra(extraKey)){
        playList.add((Song) (intent.getSerializableExtra(extraKey)));
        ++i;
      } else break;
    }
    this.playListModel = new PlayListModel(playList);
    if(DEBUG)Log.d(TAG,"onStartCommand: "+playList.size());
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(mediaPlayer != null){
      stopMedia();
      mediaPlayer.release();
      mediaPlayer = null;
      if(DEBUG)Log.d(TAG,"onDestroy");
    }
    stopSelf(); //beende Service
    removeAudioFocus();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return iBinder;
  }

  /** wird aufgerufen um den BufferStatus einer Medienresource, die über Netzwerkgestreamt wird anzuzeigen*/
  @Override
  public void onBufferingUpdate(MediaPlayer mp, int percent) {

  }

  /** wird aufgerufen wenn Medienresource fertig abgespielt wurde*/
  @Override
  public void onCompletion(MediaPlayer mp) {
    if(mediaPlayer != null){
      autoPlay();

      if(((LocalBinder) iBinder) != null){
        if(DEBUG)Log.d(TAG,"onCompltion: "+(iBinder != null));
        ((LocalBinder) iBinder).boundServiceListener.finishedSong(playListModel.repeatmode);
      }
    }
  }

  /** wird aufgerufen wenn es Fehler gibt */
  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    switch (what){
      case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
        break;
      case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
        break;
      case MediaPlayer.MEDIA_ERROR_UNKNOWN:
        break;
    }
    if(((LocalBinder) iBinder) != null)((LocalBinder) iBinder).boundServiceListener.onError(what);
    return false;
  }

  /** wird aufgerufen um uns Informationen zu geben */
  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra) {
    return false;
  }

  /** wird aufgerufen wenn medienresource bereit ist zum abspielen */
  @Override
  public void onPrepared(MediaPlayer mp) {
    playMedia();
    isMediaPlayerPrepared = true;
    //seekto if player was stopped
    if(resumePosition != 0)mediaPlayer.seekTo(resumePosition);
    mediaPlayer.setVolume(1.0f,1.0f);
  }

  /** wird aufgerufen beim abschluss einer onSeek Operation*/
  @Override
  public void onSeekComplete(MediaPlayer mp) {}

  /** aufgerufen wenn sich der Audiofokus ändert, z.B durch eingehenden Anruf */
  @Override
  public void onAudioFocusChange(int focusState) {
    if(DEBUG)Log.d(TAG,"onAudioFocusChange");
    switch (focusState){
      case AudioManager.AUDIOFOCUS_GAIN:
        //setze Wiedergabe fort
        resume();
        break;
      case AudioManager.AUDIOFOCUS_LOSS:
        //verlieren Audiofokus verloren und auf unbestimmte Zeit verloren
        if(mediaPlayer.isPlaying()){mediaPlayer.stop(); isMediaPlayerPrepared=false;}
        resumePosition = mediaPlayer.getCurrentPosition();
        break;
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        //verlieren Audiofokus für kurze,unbestimmte Zeit verloren (z.B. YouTube Wiedergabe gestartet)
        if(mediaPlayer.isPlaying())mediaPlayer.pause();
        resumePosition = mediaPlayer.getCurrentPosition();
        break;
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
        //verlieren Audiofokus für kurze Zeit (z.B. Klingelton)
        mediaPlayer.setVolume(0.1f,0.1f);
        break;
    }
    if(((LocalBinder) iBinder) != null)((LocalBinder) iBinder).boundServiceListener.onAudioFocusChange(focusState);
  }

  private boolean requestAudioFocus(){
    if(audioManager!=null){
      audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
      int result = audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
      return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    } else return true;
  }

  private boolean removeAudioFocus(){
    if(audioManager!=null)return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    else return true;
  }

  public Song getCurrentSong() {return playListModel.getCurrentSong();}
  public void seekTo(int i) {mediaPlayer.seekTo(i);}

  public void playSong(Song song) {
    playListModel.setCurrentSong(song);
    initMediaPlayer();
  }

  public void nextSong() {
    playListModel.nextSong();
    initMediaPlayer();
  }

  public void prevSong() {
    playListModel.nextSong();
    initMediaPlayer();
  }

  public boolean toogleShuffleMode() {return playListModel.toogleShuffleMode();}
  public PlayListModel.REPEATMODE nextRepeatMode() { return playListModel.nextRepeatMode();}

  /** defines what happens when a song is finnished*/
  private void autoPlay(){
    switch(playListModel.repeatmode){
      case ONESONG: {
        playSong(playListModel.getCurrentSong());
        break;
      }
      case ALL: {
        nextSong();
        break;
      }
    }

  }




  /** used to bind service to activity*/
  public class LocalBinder extends Binder {
    public MainActivityListener.BoundServiceListener boundServiceListener;

    public MediaPlayerService getService() {
      return MediaPlayerService.this;
    }

    /** set Listener-Object*/
    public void setListener(MainActivityListener.BoundServiceListener listener) {
      boundServiceListener = listener;
    }

  }
}
