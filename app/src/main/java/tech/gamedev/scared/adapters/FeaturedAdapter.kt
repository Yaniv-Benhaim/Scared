package tech.gamedev.scared.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_featured.view.*
import kotlinx.android.synthetic.main.item_featured3.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import tech.gamedev.scared.R

import javax.inject.Inject

class FeaturedAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseFeaturedAdapter(R.layout.item_featured3) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvSubtitle.text = song.subtitle
            tvTitle.text = song.title
            glide.load(song.imageUrl).into(ivFeaturedStory)
            btnPlayFeatured.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}