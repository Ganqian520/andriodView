package com.gq.music.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPutil {
  private SharedPreferences sp;
  private SharedPreferences.Editor editor;
  public SPutil(Context context){
    sp = context.getSharedPreferences("",Context.MODE_PRIVATE);
    editor = sp.edit();
  }
  public String getString(String key){
    return sp.getString(key,"");
  }
  public void putString(String key,String value){
    editor.putString(key,value).commit();
  }
}
