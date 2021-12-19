package com.gq.video.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gq.video.room.entities.Video

@Dao
interface DaoVideo {
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertVideo(video: Video)
  
  @Query("SELECT * FROM videos ORDER BY id_ DESC")
  fun selectVideoAll():List<Video>
  
}