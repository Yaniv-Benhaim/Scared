package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.VideoAdapter
import tech.gamedev.scared.adapters.VideoViewpagerAdapter
import tech.gamedev.scared.databinding.FragmentVideoBinding
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import tech.gamedev.scared.ui.viewmodels.VideoViewModel
import javax.inject.Inject


@AndroidEntryPoint
class VideoFragment : Fragment(R.layout.fragment_video), VideoAdapter.OnVideoClickedListener {

    private val videoViewModel: VideoViewModel by activityViewModels()
    private lateinit var binding: FragmentVideoBinding
    private lateinit var documentaryAdapter: VideoAdapter
    private lateinit var redditAdapter: VideoAdapter
    private lateinit var viewPager: VideoViewpagerAdapter

    private var signInVisible = false
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var glide: RequestManager


    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideoBinding.bind(view)

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

    }

    private fun subscribeToObservers() {

        videoViewModel.allVideos.observe(viewLifecycleOwner) {
            viewPager = VideoViewpagerAdapter(it)
            setViewPager()
        }

        videoViewModel.documentaries.observe(viewLifecycleOwner) {
            documentaryAdapter = VideoAdapter(it, this)
            setDocumentaryRV()
        }

        videoViewModel.redditVideos.observe(viewLifecycleOwner) {
            redditAdapter = VideoAdapter(it, this)
            setRedditRV()
        }

        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvName.text = it.displayName
                glide.load(it.photoUrl).into(binding.ivProfileImg)
            } else {
                binding.ivProfileImg.setImageResource(R.drawable.ic_account)
                binding.tvName.text = getString(R.string.not_signed_in)
            }

        }
    }

    private fun setDocumentaryRV() = binding.rvDocumentaries.apply {
        adapter = documentaryAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setRedditRV() = binding.rvReddit.apply {
        adapter = redditAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setViewPager() = binding.vpAllVideos.apply {
        adapter = viewPager
    }

    override fun onVideoClick(title: String, description: String, videoUrl: String) {

        val action =
            VideoFragmentDirections.globalActionToVideoPlayFragment(title, description, videoUrl)
        findNavController().navigate(action)
    }
}