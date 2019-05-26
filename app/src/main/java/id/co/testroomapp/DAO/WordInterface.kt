package id.co.testroomapp.DAO


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import id.co.testroomapp.Entity.Word

@Dao
interface WordDao {

    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAllWords(): LiveData<List<Word>>

//    @Query("SELECT * from word_table WHERE wordId = :wordId")
//    fun getWord(wordId:String)

    @Insert
    fun insert(word: Word)

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Query("DELETE FROM word_table WHERE word = :word")
    fun deleteWord(word:String)

    @Update
    fun updateWord(word:Word)

}