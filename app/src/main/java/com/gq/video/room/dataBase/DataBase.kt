package com.gq.video.room.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gq.music.MyApplication
import com.gq.video.room.dao.DaoVideo
import com.gq.video.room.entities.Video

@Database(entities = [Video::class],version = 1,exportSchema = false)
abstract class DataBase : RoomDatabase() {
  
  abstract fun getDaoVideo():DaoVideo
  
  companion object {
    val instance = Single.sin
  }
  
  private object Single {
    val sin:DataBase = Room.databaseBuilder(MyApplication.context,DataBase::class.java,"videos.db")
      .allowMainThreadQueries()
      .build()
  }

//  companion object {
//
//    var databaseVideo:DataBase? = null
//
//    @Synchronized
//    fun getDataBase(context: Context):DataBase{
//      if (databaseVideo!=null){
//        databaseVideo = Room.databaseBuilder(context,DataBase::class.java,"videos.db").build()
//      }
//      return databaseVideo!!
//    }
//  }

}