package com.gq.learnJP.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gq.learnJP.room.entity.MyCollection
import com.gq.learnJP.room.entity.Word

@Dao
interface Dao {
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWords(words:ArrayList<Word>)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertMyCollection(collections:ArrayList<MyCollection>)
  
  @Query("SELECT * FROM JPwords WHERE jp in (:jps) ORDER BY id DESC")
  fun selectWords(jps:List<String>):List<Word>
  
  @Query("SELECT * FROM MyCollection WHERE collection=(:collection)")
  fun selectCollection(collection:String):MyCollection
  
  @Query("SELECT collection FROM MyCollection")
  fun selectCollectionNames():List<String>
  
  //清空句集
  @Query("DELETE FROM MyCollection")
  fun clearMyCollection()
  //清空单词集
  @Query("DELETE FROM JPwords")
  fun clearWords()
  
}