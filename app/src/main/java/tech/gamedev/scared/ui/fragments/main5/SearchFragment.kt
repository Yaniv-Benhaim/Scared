package tech.gamedev.scared.ui.fragments.main5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import tech.gamedev.scared.R
import tech.gamedev.scared.databinding.FragmentSearchBinding
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    @Inject
    lateinit var glide: RequestManager

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        subscribeToObservers()


    }

    private fun subscribeToObservers(){

        loginViewModel.user.observe(viewLifecycleOwner) {
            binding.tvName.text = it.displayName
            glide.load(it.photoUrl).into(binding.ivProfileImg)

        }
    }

}