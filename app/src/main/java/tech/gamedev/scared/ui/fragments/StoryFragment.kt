package tech.gamedev.scared.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import tech.gamedev.scared.R
import tech.gamedev.scared.databinding.FragmentStoryBinding


class StoryFragment : Fragment(R.layout.fragment_story) {

    private val args: StoryFragmentArgs by navArgs()
    lateinit var binding: FragmentStoryBinding

    lateinit var storyTitle: String
    lateinit var storyText: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStoryBinding.bind(view)

        storyTitle = args.title
        storyText = args.story

        binding.tvStoryTitle.text = storyTitle
        binding.tvStoryText.text = storyText

    }

}