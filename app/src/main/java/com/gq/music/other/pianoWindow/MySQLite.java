package com.gq.music.other.pianoWindow;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySQLite extends SQLiteOpenHelper {

  private final String table_piano_window= "pianoWindow";

  public MySQLite(Context context){
    super(context,"pianoWindow.db",null,1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql1 = " create table " + table_piano_window + "(id integer primary key autoincrement,name text,str text,time text)";
    System.out.println("oncreate");
    db.execSQL(sql1);
  }

  public void updatePiano(String time,String str,String name){
    SQLiteDatabase db = getWritableDatabase();
    String sql = "update pianoWindow set str=?,name=? where time=? ";
    db.execSQL(sql,new Object[]{str,name,time});
  }

  public void addPiano(String name,String str,String time){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("insert into "+table_piano_window+" (name,str,time) values (?,?,?) ",new Object[]{name,str,time});
  }

  public Map getPiano(String time, int flag){
    SQLiteDatabase db = getReadableDatabase();
    HashMap map = new HashMap();
    Cursor cursor = null;
    if (flag==0){ //按条件查询一条
      cursor = db.rawQuery("select str from pianoWindow where time=?",new String[]{time});
      cursor.moveToFirst();
      String res = cursor.getString(cursor.getColumnIndex("str"));
      map.put("song",res);
    }else if(flag==1){  //全部，只要name和time字段
      cursor = db.rawQuery("select name,time from pianoWindow order by id desc",new String[]{});
      ArrayList list = new ArrayList();
      while (cursor.moveToNext()){
        String name_res = cursor.getString(cursor.getColumnIndex("name"));
        String time_res = cursor.getString(cursor.getColumnIndex("time"));
        HashMap _map = new HashMap();
        _map.put("name",name_res);
        _map.put("time",time_res);
        list.add(_map);
      }
      map.put("songs",list);
    }else if(flag==2){  //最后一条，最新的
      cursor = db.rawQuery("select * from pianoWindow order by id desc limit 1",new String[]{});
      if(cursor.moveToLast()){
        String res_str = cursor.getString(cursor.getColumnIndex("str"));
        map.put("song",res_str);
      }
    }
    cursor.close();
    return map;
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
