package tech.gamedev.scared.ui.fragments.splash

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_splash.*
import tech.gamedev.scared.R


class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_movieFragment)
        }

        /*val intro: Uri = Uri.parse("android.resource://tech.gamedev.scared/raw/scary_intro2")

        vvSplash.setVideoURI(intro)
        vvSplash.start()*/

    }
}