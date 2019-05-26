package id.co.testroomapp.LiveData

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import id.co.testroomapp.DAO.WordDao
import id.co.testroomapp.Database.WordRoomDatabase
import id.co.testroomapp.Entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordRepository(private val wordDao: WordDao) {
    val allWords: LiveData<List<Word>> = wordDao.getAllWords()

    @WorkerThread
    fun insert(word: Word) {
        wordDao.insert(word)
    }
    fun delete(word: Word){
        wordDao.deleteWord(word)
    }
    fun update(word: Word){
        wordDao.updateWord(word)
    }
}

class WordViewModel(application: Application) : AndroidViewModel(application){

    private val repository: WordRepository
    val allWords: LiveData<List<Word>>

    init {
        val wordsDao = WordRoomDatabase.getDatabase(application, viewModelScope).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords
    }

    fun update(word: Word) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(word)
    }

    fun insert(word: Word) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(word)
    }

    fun delete(word: Word) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(word)
    }

}