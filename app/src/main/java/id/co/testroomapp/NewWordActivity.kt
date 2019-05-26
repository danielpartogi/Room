package id.co.testroomapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.room.Update
import id.co.testroomapp.Entity.Word
import id.co.testroomapp.LiveData.WordViewModel
import id.co.testroomapp.WordListAdapter.Companion.updateCode
import java.util.*

class NewWordActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText
    private lateinit var  word:String
    private lateinit var  wordModel:Word


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)

        editWordView = findViewById(R.id.edit_word)

        if(intent.hasExtra("word")){
            wordModel = intent?.extras?.get("word") as Word
            editWordView.setText(wordModel.word)
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            word = editWordView.text.toString()

            when {
                TextUtils.isEmpty(editWordView.text) -> {

                }
                intent.hasExtra("word") -> {
                    wordModel.word = word
                    replyIntent.putExtra(EXTRA_UPDATE, wordModel)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                else -> {
                    replyIntent.putExtra(EXTRA_REPLY, word)
        //
                    setResult(Activity.RESULT_OK, replyIntent)
                }
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_UPDATE = "UPDATE"
    }
}