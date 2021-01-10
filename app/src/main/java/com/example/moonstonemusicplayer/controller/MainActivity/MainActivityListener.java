package com.example.moonstonemusicplayer.controller.MainActivity;

import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.example.moonstonemusicplayer.R;
import com.example.moonstonemusicplayer.model.MainActivity.FolderFragment.Folder;
import com.example.moonstonemusicplayer.model.MainActivity.FolderFragment.FolderManager;
import com.example.moonstonemusicplayer.view.MainActivity;
import com.example.moonstonemusicplayer.view.ui.main.FolderFragment;

public class MainActivityListener {
  private final MainActivity mainActivity;
  private Fragment[] fragments;


  public MainActivityListener(MainActivity mainActivity,Fragment[] fragments) {
    this.mainActivity = mainActivity;
    this.fragments = fragments;
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    //create options menu
    mainActivity.getMenuInflater().inflate(R.menu.options_menu_mainactivity,menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.mi_loadLocaleAudioFile: {
        ((FolderFragment) fragments[1]).loadMusicAsFolders();
        //folderManager.getRootFolder().print(0);
        break;
      }
      case R.id.miDeleteAllAudioFiles: {

        break;
      }
    }
    //songListAdapter.notifyDataSetChanged();
    return true;
  }
}
