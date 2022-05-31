package com.gq.learnJP.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MyCollection")
data class MyCollection(
  @PrimaryKey(autoGenerate = true)
  var id:Int,
  
  @ColumnInfo(name = "collection")
  var collection:String,
  
  @ColumnInfo(name = "sentences")
  var sentences:String
)