package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_movie.*
import tech.gamedev.scared.R
import tech.gamedev.scared.data.models.Video
import tech.gamedev.scared.databinding.FragmentMovieBinding
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeaturedFragment : Fragment(R.layout.fragment_featured) {

    @Inject
    lateinit var glide: RequestManager
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentMovieBinding
    lateinit var videos: ArrayList<Video>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it.displayName != null) {


                binding.tvName.text = it.displayName.toString()
                glide.load(it.photoUrl).into(binding.ivProfileImg)

            }
        }

        /*videos = ArrayList()

        videos.add(Video("https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Crime%20Scene%20Cleaners%20Share%20Bizarre%20Things%20They%20Have%20Seen%20-%20AskReddit.mp4?alt=media&token=3b65dcfe-3826-4eb3-9ece-d97d605d65ae","Cleaners Share Bizarre Things They Have Seen","Reddit Stories",5))
        videos.add(Video("https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Crime%20Scene%20Photographers%20Share%20Their%20Most%20Traumatizing%20Stories%20-%20AskReddit.mp4?alt=media&token=262ac97c-850a-4e4c-adc9-96b8ef39b724","Crime Scene Photographers Share Their Most Traumatizing Stories","Reddit Stories",5))
        videos.add(Video("https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Crime%20Scene%20Workers%20Share%20The%20Most%20Disturbing%20Cases%20%20-%20AskReddit.mp4?alt=media&token=fb7a4e54-a091-4b30-86cc-29f34716ef67","Crime Scene Photographers Share Their Most Traumatizing Stories","Reddit Stories",5))



        vvScaryVideos.apply {

            adapter = VideoRecyclerView(videos)
            orientation = ORIENTATION_VERTICAL

        }*/
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null) {
            loginViewModel.assignUser(user)
        }
    }
}

