package tech.gamedev.scared.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_video.view.*
import tech.gamedev.scared.R
import tech.gamedev.scared.data.models.Video

class VideoAdapter(
    private val videos: List<Video>,
    private val listener: OnVideoClickedListener
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun initialize(
            title: String,
            description: String,
            videoUrl: String,
            action: OnVideoClickedListener
        ) {
            itemView.setOnClickListener {
                action.onVideoClick(title, description, videoUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_video,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        holder.itemView.apply {
            tvVideoTitle.text = video.title
            Glide.with(context).load(video.thumbnailUrl).into(ivThumbnail)
        }

        holder.initialize(video.title, video.description, video.url, listener)

    }

    override fun getItemCount(): Int {
        return videos.size
    }

    interface OnVideoClickedListener {
        fun onVideoClick(title: String, description: String, videoUrl: String)
    }
}