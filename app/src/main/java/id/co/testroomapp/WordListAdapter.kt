package id.co.testroomapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.co.testroomapp.Entity.Word
import id.co.testroomapp.LiveData.WordViewModel
import android.app.Activity
import java.io.Serializable


class WordListAdapter internal constructor(
    var context: Context
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = emptyList<Word>() // Cached copy of words

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val wordItemView: TextView = itemView.findViewById(R.id.textView)
        val cvParent: CardView = itemView.findViewById(R.id.cv_parent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = words[position]
        holder.wordItemView.text = current.word
        holder.cvParent.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Update")
            builder.setMessage("Update Kata?")
            builder.setPositiveButton("YA") { _, _ ->
                val intent = Intent(this.context, NewWordActivity::class.java)
                intent.putExtra("word", current as Serializable)

                (context as Activity).startActivityForResult(intent, updateCode)
            }
            builder .setNegativeButton("Tidak") { _, _ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()


        }

        holder.cvParent.setOnLongClickListener {
            Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show()
            WordViewModel(Singleton()).delete(current)
            true
        }

    }


    companion object {
        const val updateCode = 201
    }



    internal fun setWords(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }



    override fun getItemCount() = words.size
}