package com.example.libfolder.notused.RadioFragment;

import android.content.Context;

//import com.example.moonstonemusicplayer.model.Database.DBSonglists;

import java.util.ArrayList;
import java.util.List;

public class RadioManager {

  private static final String TAG = RadioManager.class.getSimpleName();
  private final Context context;
  //private DataSource dataSource;

  private final List<Radio> radiolists = new ArrayList<>();

  public RadioManager(Context baseContext) {
    this.context = baseContext;
    loadRadiosFromDB(baseContext);
  }

  /** loads local music and adds it to dataSource*/
  public void loadRadiosFromDB(Context context){
    if(context != null){
      //this.radiolists.addAll(DBSonglists.getInstance(context).getAllRadios());
    }
  }

  public List<Radio> getAllRadios(){
    return this.radiolists;
  }
}
