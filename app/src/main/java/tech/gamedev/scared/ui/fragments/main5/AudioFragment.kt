package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.android.synthetic.main.fragment_profile.*
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.AudioAdapter
import tech.gamedev.scared.databinding.FragmentAudioBinding
import tech.gamedev.scared.other.Status
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import tech.gamedev.scared.ui.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AudioFragment : Fragment(R.layout.fragment_audio) {

    lateinit var mainViewModel: MainViewModel

    private val loginViewModel: LoginViewModel by activityViewModels()

    @Inject
    lateinit var audioAdapter: AudioAdapter

    @Inject
    lateinit var glide: RequestManager

    private lateinit var binding: FragmentAudioBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAudioBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()

        loginViewModel.user.observe(viewLifecycleOwner) {
            if(it != null){
                binding.tvNameAudio.text = it.displayName
                glide.load(it.photoUrl).into(binding.ivProfileImg)

            }
        }

        audioAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = binding.rvAllSongs.apply {
        adapter = audioAdapter
        layoutManager = GridLayoutManager(requireContext(),2)
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
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