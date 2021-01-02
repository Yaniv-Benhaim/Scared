package tech.gamedev.scared.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_story.view.*
import tech.gamedev.scared.R
import tech.gamedev.scared.data.models.Story

class StoryAdapter(
    private val stories: List<Story>,
    val context: Context,
    private val clickedListener: OnStoryClickedListener) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun initialize(title: String, story: String, action: OnStoryClickedListener){
            itemView.setOnClickListener {
                action.onStoryClick(title, story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_story, parent, false))
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]

        holder.itemView.apply {
            tvStoryName.text = story.title

            Glide.with(context).load(story.imageUrl).into(ivStoryCover)
        }

        holder.initialize(story.title, story.story, clickedListener)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    interface OnStoryClickedListener {
        fun onStoryClick(title: String, story:String)
    }
}


