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
import java.util.*

class NewWordActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText
    private lateinit var  word:String
    private lateinit var  wordId:String


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)

        var update = false
        editWordView = findViewById(R.id.edit_word)
        if(intent.hasExtra("Word"))
        {
            word = intent.getStringExtra("Word")
        }
        if(intent.hasExtra("updateWord"))
        {
            update = intent.getBooleanExtra("updateWord", false)
            wordId = intent.getStringExtra("wordId")
        }
        if(update){
            editWordView.setText(word)
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                if(update){
                    val wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
                        wordViewModel.update(wordId,word)
                }
                else{
                    replyIntent.putExtra(EXTRA_REPLY, word)
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}