package tech.gamedev.scared.adapters
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_featured3.view.*
import kotlinx.android.synthetic.main.swipe_item.view.*
import tech.gamedev.scared.R
import javax.inject.Inject


class SwipeFeaturedAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.item_featured3) {



    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvTitle.text = song.title
            tvSubtitle.text = song.subtitle
            glide.load(song.imageUrl).into(ivFeaturedStory)



            setOnClickListener {
                Toast.makeText(context, "CLICK WORKING", Toast.LENGTH_SHORT).show()
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}



















