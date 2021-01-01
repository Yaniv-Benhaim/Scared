package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_audio.*
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.FeaturedAdapter
import tech.gamedev.scared.adapters.SwipeFeaturedAdapter
import tech.gamedev.scared.adapters.SwipeSongAdapter
import tech.gamedev.scared.data.models.Song
import tech.gamedev.scared.data.models.Video
import tech.gamedev.scared.databinding.FragmentFeaturedBinding
import tech.gamedev.scared.exoplayer.isPlaying
import tech.gamedev.scared.exoplayer.toSong
import tech.gamedev.scared.other.Status
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import tech.gamedev.scared.ui.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeaturedFragment : Fragment(R.layout.fragment_featured) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var featuredAdapter: FeaturedAdapter

    @Inject
    lateinit var swipeSongAdapter: SwipeFeaturedAdapter
    private var curPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentFeaturedBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeaturedBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        setupFeaturedViewPager()
        subscribeToObservers()

        binding.vpFeatured3.adapter = swipeSongAdapter
        binding.vpFeatured3.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        binding.btnPlayFeatured.setOnClickListener {
            Toast.makeText(requireContext(), "PLAY BTN WORKING", Toast.LENGTH_SHORT).show()
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            Toast.makeText(requireContext(), "CLICK WORKING", Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            binding.vpFeatured3.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers(){

        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it.displayName != null) {
                binding.tvName.text = it.displayName.toString()
                glide.load(it.photoUrl).into(binding.ivProfileImg)
            }
        }

        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            /*if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(ivCurSongImage)
                            }*/
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            /*glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)*/
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
        }

        mainViewModel.isConnected.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                            rootLayout,
                            result.message ?: "An unknown error occured",
                            Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
        mainViewModel.networkError.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                            rootLayout,
                            result.message ?: "An unknown error occured",
                            Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }






    }

    private fun setupFeaturedViewPager() = binding.vpFeatured3.apply {
        adapter = featuredAdapter

    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null) {
            loginViewModel.assignUser(user)
        }
    }
}

