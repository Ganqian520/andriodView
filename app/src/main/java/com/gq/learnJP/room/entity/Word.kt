package com.gq.learnJP.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "JPwords")
data class Word (
  @PrimaryKey(autoGenerate = true)
  var id: Int,

  @ColumnInfo(name = "jp")
  var jp: String,

  @ColumnInfo(name = "cn")
  var cn: String,
  
  @ColumnInfo(name = "jm")
  var jm: String
) {

}
