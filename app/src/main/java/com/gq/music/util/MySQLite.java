package com.gq.music.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLite extends SQLiteOpenHelper {

  private final String table_lyric= "lyric";

  public MySQLite(Context context){
    super(context,"gqSQLite.db",null,2);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql1 = " create table " + table_lyric + "(id integer primary key autoincrement,sid text,str text)";
    db.execSQL(sql1);
  }

  public long addLyric(String sid,String str){
    ContentValues values = new ContentValues();
    SQLiteDatabase db = getWritableDatabase();
    values.put("sid",sid);
    values.put("str",str);
    return db.insert(table_lyric,null,values);
  }

  public String getLyric(String sid){
    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.query(table_lyric,new String[]{"str"},"sid="+sid,null,null,null,null);
    while (cursor.moveToNext()){
      String str = cursor.getString(cursor.getColumnIndex("str"));
      cursor.close();
      return str;
    }
    return "notFound";
  }

  public String getComments(String sid){
    SQLiteDatabase db = getWritableDatabase();
    Cursor cursor = db.rawQuery("select comments from lyric where sid=?",new String[]{sid});
    while (cursor.moveToNext()){
      String str = cursor.getString(cursor.getColumnIndex("comments"));
      return str;
    }
    return "notFound";
  }



  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
