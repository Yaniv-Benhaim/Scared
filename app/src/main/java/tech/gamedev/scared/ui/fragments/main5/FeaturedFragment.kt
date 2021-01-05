package tech.gamedev.scared.ui.fragments.main5

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.facebook.ads.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.android.synthetic.main.fragment_featured.*
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.*
import tech.gamedev.scared.data.models.Song
import tech.gamedev.scared.databinding.FragmentFeaturedBinding
import tech.gamedev.scared.exoplayer.isPlaying
import tech.gamedev.scared.exoplayer.toSong
import tech.gamedev.scared.other.Constants
import tech.gamedev.scared.other.Status
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import tech.gamedev.scared.ui.viewmodels.MainViewModel
import tech.gamedev.scared.ui.viewmodels.StoryViewModel
import tech.gamedev.scared.ui.viewmodels.VideoViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FeaturedFragment : Fragment(R.layout.fragment_featured), StoryAdapter.OnStoryClickedListener, VerticalVideoAdapter.OnVideoClickedListener {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var featuredAdapter: FeaturedAdapter

    @Inject
    lateinit var audioAdapter: AudioAdapter

    @Inject
    lateinit var swipeSongAdapter: SwipeFeaturedAdapter
    lateinit var storyAdapter: StoryAdapter
    private lateinit var verticalVideoAdapter: VerticalVideoAdapter

    private lateinit var interstitialAd: InterstitialAd
    private var curAdsDisplayed = 0

    private var curPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val storyViewModel: StoryViewModel by activityViewModels()
    private val videoViewModel: VideoViewModel by activityViewModels()


    private lateinit var binding: FragmentFeaturedBinding

    private var signInVisible = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = FragmentFeaturedBinding.bind(view)
        //SET FACEBOOK AD
        AudienceNetworkAds.initialize(requireContext())
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        setupFeaturedViewPager()
        subscribeToObservers()

        binding.ivProfileImg.setOnClickListener {
            when (signInVisible) {
                false -> {
                    if (loginViewModel.user.value != null) {
                        binding.btnSignInOrOut.text = getString(R.string.sign_out)
                    } else {
                        binding.btnSignInOrOut.text = getString(R.string.sign_in)
                    }
                    signInVisible = true
                    binding.cvSignInOrOut.isVisible = true
                }
                true -> {
                    signInVisible = false
                    binding.cvSignInOrOut.isVisible = false
                }
            }
        }

        binding.btnSignInOrOut.setOnClickListener {
            if (binding.btnSignInOrOut.text == getString(R.string.sign_in)) {
                findNavController().navigate(R.id.globalActionToProfileFragment)
            } else {
                auth.signOut()
                loginViewModel.signOut()
            }
        }



        binding.vpFeatured3.adapter = swipeSongAdapter
        binding.vpFeatured3.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        binding.btnPlayFeatured.setOnClickListener {
            setFacebookAd()
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            setFacebookAd()
            findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        audioAdapter.setItemClickListener {
            setFacebookAd()
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = binding.rvAudioStories.apply {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager = linearLayoutManager
        adapter = audioAdapter

    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            binding.vpFeatured3.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {

        mainViewModel.adsDisplayed.observe(viewLifecycleOwner) {
            if (it == null) {
                mainViewModel.setCurAdsDisplayedToZero()
            } else {
                curAdsDisplayed = it
            }
        }

        videoViewModel.allVideos.observe(viewLifecycleOwner) {
            verticalVideoAdapter = VerticalVideoAdapter(it, this)
            setVideoRecyclerView()
        }

        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvName.text = it.displayName.toString()
                glide.load(it.photoUrl).into(binding.ivProfileImg)
            } else {
                binding.ivProfileImg.setImageResource(R.drawable.ic_account)
                binding.tvName.text = getString(R.string.not_signed_in)
            }
        }

        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            audioAdapter.songs = songs
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

        storyViewModel.stories.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                storyAdapter = StoryAdapter(it, requireContext(), this)
                setupBookRecyclerView()

            }
        }
    }

    private fun setVideoRecyclerView() = binding.rvVideoStories.apply {
        adapter = verticalVideoAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun setupFeaturedViewPager() = binding.vpFeatured3.apply {
        adapter = featuredAdapter
    }

    private fun setupBookRecyclerView() = binding.rvStories.apply {

        val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager = linearLayoutManager
        adapter = storyAdapter
    }

    override fun onStoryClick(title: String, story: String) {
        setFacebookAd()
        val action = FeaturedFragmentDirections.actionFeaturedFragmentToStoryFragment(title, story)
        findNavController().navigate(action)
    }

    override fun onVideoClick(title: String, description: String, videoUrl: String) {
        setFacebookAd()
        val action = FeaturedFragmentDirections.globalActionToVideoPlayFragment(title, description, videoUrl)
        findNavController().navigate(action)
    }

    private fun setFacebookAd() {

        interstitialAd = InterstitialAd(requireContext(), Constants.FACEBOOK_PLACEMENT_ID)

        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                Timber.e("Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                // Interstitial dismissed callback
                Timber.e("Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Ad error callback
                Timber.e("Interstitial ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                Timber.d("Interstitial ad is loaded and ready to be displayed!")
                // Show the ad

                if (curAdsDisplayed % 3 == 0 && curAdsDisplayed != 0) {
                    interstitialAd.show()
                    mainViewModel.increaseAdsDisplayed()
                    Log.d("AD", "ADD DISPLAYED: $curAdsDisplayed")
                } else {
                    mainViewModel.increaseAdsDisplayed()
                    Log.d("AD", "ADD NOT DISPLAYED: $curAdsDisplayed")
                }


            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
                Timber.d("Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
                Timber.d("Interstitial ad impression logged!")
            }
        }

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build()
        )


    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            loginViewModel.assignUser(user)
        }
    }
}

