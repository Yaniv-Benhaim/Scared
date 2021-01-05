package tech.gamedev.scared.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_video_vp.view.*
import tech.gamedev.scared.R
import tech.gamedev.scared.data.models.Video

class VideoViewpagerAdapter(private val videos: List<Video>) :
    RecyclerView.Adapter<VideoViewpagerAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_video_vp,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        holder.itemView.apply {
            tvTitle.text = video.title
            Glide.with(context).load(video.thumbnailUrl).into(ivThumbnailVP)
        }

    }

    override fun getItemCount(): Int {
        return videos.size
    }
}