package com.gq.learnJP.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gq.learnJP.room.entity.MyCollection
import com.gq.learnJP.room.entity.Word
import com.gq.music.MyApplication

@Database(entities = [Word::class,MyCollection::class],version = 1,exportSchema = false)
abstract class DataBase : RoomDatabase() {
  abstract fun getDao(): Dao
  
  companion object {
    val instance = Single.sin
  }
  
  private object Single {
    val sin: DataBase = Room.databaseBuilder(MyApplication.context, DataBase::class.java,"learnJP.db")
      .allowMainThreadQueries()
      .build()
  }
}