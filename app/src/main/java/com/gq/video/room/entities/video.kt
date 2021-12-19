package com.gq.video.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class Video (
  @PrimaryKey(autoGenerate = true)
  var id_: Int,
  
  
  @ColumnInfo(name = "createTime")
  var createTime:Long,  //api视频创建时间
  @ColumnInfo(name = "id")
  var id:String, //抖音api的id 存起以防不测
  @ColumnInfo(name = "url")
  var url: String,//播放地址
  @ColumnInfo(name = "img")
  var img: String,  //封面
  @ColumnInfo(name = "desc")
  var desc:String, //api里的文案
  @ColumnInfo(name = "duration")
  var duration:Int,  //时长,秒

  @ColumnInfo(name = "describe")
  var describe: String, //自己加的描述
  @ColumnInfo(name = "tag")
  var tag: String  //标签

)
