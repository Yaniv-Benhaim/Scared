package tech.gamedev.scared.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import tech.gamedev.scared.R
import tech.gamedev.scared.databinding.FragmentVideoPlayBinding
import tech.gamedev.scared.ui.viewmodels.VideoViewModel


class VideoPlayFragment : Fragment(R.layout.fragment_video_play) {

    private val args: VideoPlayFragmentArgs by navArgs()
    private val videoViewModel: VideoViewModel by activityViewModels()
    private var isFullScreen: Boolean = false
    private var backPressed: Int = 0
    lateinit var title: String
    lateinit var description: String
    lateinit var videoUrl: String


    lateinit var binding: FragmentVideoPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            backPressed++
            if (backPressed < 2) {
                Toast.makeText(requireContext(), "Press 1 more time to go back", Toast.LENGTH_SHORT)
                    .show()
            } else {
                findNavController().navigate(R.id.globalActionToVideoFragment)
            }
        }

        callback.isEnabled
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideoPlayBinding.bind(view)

        subscribeToObservers()

        title = args.title
        description = args.description
        videoUrl = args.videoUrl

        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = videoUrl
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        binding.youtubePlayerView.enableBackgroundPlayback(true)

        lifecycle.addObserver(binding.youtubePlayerView)


        binding.tvTitle.text = title
        binding.tvDescription.text = description


    }

    private fun subscribeToObservers() {
        videoViewModel.isFullScreen.observe(viewLifecycleOwner) {
            isFullScreen = it
        }
    }


}