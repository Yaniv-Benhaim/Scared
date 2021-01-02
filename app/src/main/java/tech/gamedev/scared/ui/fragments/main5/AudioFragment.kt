package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.facebook.ads.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio.*
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.AudioAdapter
import tech.gamedev.scared.databinding.FragmentAudioBinding
import tech.gamedev.scared.other.Constants
import tech.gamedev.scared.other.Status
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import tech.gamedev.scared.ui.viewmodels.MainViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AudioFragment : Fragment(R.layout.fragment_audio) {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val loginViewModel: LoginViewModel by activityViewModels()

    @Inject
    lateinit var audioAdapter: AudioAdapter

    private var curAdsDisplayed = 0

    @Inject
    lateinit var glide: RequestManager

    private lateinit var interstitialAd: InterstitialAd

    private lateinit var binding: FragmentAudioBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAudioBinding.bind(view)

        setupRecyclerView()
        subscribeToObservers()

        //SET FACEBOOK AD
        AudienceNetworkAds.initialize(requireContext())


        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvNameAudio.text = it.displayName
                glide.load(it.photoUrl).into(binding.ivProfileImg)

            }
        }

        audioAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
            setFacebookAd()
        }
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

    private fun setupRecyclerView() = binding.rvAllSongs.apply {
        adapter = audioAdapter
        layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun subscribeToObservers() {

        mainViewModel.adsDisplayed.observe(viewLifecycleOwner) {
            if (it == null) {
                mainViewModel.setCurAdsDisplayedToZero()
            } else {
                curAdsDisplayed = it
            }
        }


        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
                        audioAdapter.songs = songs
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }
}