package tech.gamedev.scared.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_video.view.*
import tech.gamedev.scared.R
import tech.gamedev.scared.data.models.Video

class VideoRecyclerView(val videos: List<Video>) : RecyclerView.Adapter<VideoRecyclerView.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_video,
                parent,
                false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        val intro: Uri = Uri.parse(video.url)
        holder.itemView.apply {
            vvVideoPlayer.setVideoURI(intro)
            vvVideoPlayer.start()
        }

    }

    override fun getItemCount(): Int {
        return videos.size
    }
}