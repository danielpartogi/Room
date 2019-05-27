package id.co.testroomapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.co.testroomapp.Entity.Word
import id.co.testroomapp.LiveData.WordViewModel
import id.co.testroomapp.WordListAdapter.Companion.updateCode
import java.util.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.audiofx.DynamicsProcessing
import androidx.appcompat.graphics.drawable.DrawableWrapper
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.core.graphics.drawable.DrawableCompat
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.Serializable
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(){
    private lateinit var wordViewModel: WordViewModel
    private val p = Paint()
    private var adapter: WordListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = WordListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter!!.setWords(it) }
        })
        val fab: View = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

      initSwipe()
    }
    companion object {
        const val newWordActivityRequestCode = 1
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                val word = Word(UUID.randomUUID().toString(),it.getStringExtra(NewWordActivity.EXTRA_REPLY))
                wordViewModel.insert(word)
            }
        }
        else if (requestCode == updateCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                wordViewModel.update(it.extras?.get(NewWordActivity.EXTRA_UPDATE) as Word)
            }
        }
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    val deletedModel = wordViewModel.allWords.value!![position]
                    val deletedPosition = position
                    var deleted = true
                    adapter!!.removeItem(position)
                    val snackbar =
                        Snackbar.make(findViewById(R.id.recyclerview), " removed!", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") {
                        // undo is selected, restore the deleted item
                        adapter!!.restoreItem(deletedModel, deletedPosition)
                        deleted = false
                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                    Timer().schedule(4000){
                        if(deleted){
                            wordViewModel.delete(deletedModel)
                        }
                    }
                }
                else if (direction == ItemTouchHelper.RIGHT){
                    val intent = Intent(this@MainActivity, NewWordActivity::class.java)
                    val word = wordViewModel.allWords.value!![position]
                    intent.putExtra("word", word as Serializable)
                    intent.putExtra("position", position)

                    startActivityForResult(intent, updateCode)
                    adapter!!.removeItem(position)
                    adapter!!.restoreItem(word, position)
                }

            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#F9F9F9")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = getBitmapFromVectorDrawable(this@MainActivity,R.drawable.ic_edit_black_24dp)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.color = Color.parseColor("#F9F9F9")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = getBitmapFromVectorDrawable(this@MainActivity,R.drawable.ic_delete_forever_black_24dp)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

}







