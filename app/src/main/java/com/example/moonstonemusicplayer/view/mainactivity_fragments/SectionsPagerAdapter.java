package com.example.moonstonemusicplayer.view.mainactivity_fragments;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.moonstonemusicplayer.R;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

  @StringRes
  private static final int[] TAB_TITLES = new int[]{R.string.tab_folders,R.string.tab_playlists,R.string.tab_albums,R.string.tab_artists,R.string.tab_genre};
  private final Context mContext;

  Fragment[] fragments = new Fragment[5];

  public SectionsPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
  }

  @Override
  public Fragment getItem(int position) {
    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    if(position == 0){
      fragments[0] = FolderFragment.newInstance(position + 1);
      return fragments[0];
    } else if(position == 1){
      fragments[1] = PlayListFragment.newInstance(position + 1);
      return fragments[1];
    } else if(position == 2){
      fragments[2] = AlbumFragment.newInstance(position + 1);
      return fragments[2];
    } else if(position == 3){
      fragments[3] = ArtistFragment.newInstance(position + 1);
      return fragments[3];
    } else if(position == 4){
      fragments[4] = GenreFragment.newInstance(position + 1);
      return fragments[4];
    }
    return null;
  }

  public Fragment[] getFragments(){
    return fragments;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return mContext.getResources().getString(TAB_TITLES[position]);
  }

  @Override
  public int getCount() {
    return fragments.length;
  }


}