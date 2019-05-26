package id.co.testroomapp.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "word_table")
data class Word(@PrimaryKey @ColumnInfo(name = "wordId") val wordId:String, @ColumnInfo(name = "word") var word:String) : Serializable

