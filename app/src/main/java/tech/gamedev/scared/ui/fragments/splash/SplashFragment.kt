package tech.gamedev.scared.ui.fragments.splash

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_splash.*
import tech.gamedev.scared.R


class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_movieFragment)
        }

        /*val intro: Uri = Uri.parse("android.resource://tech.gamedev.scared/raw/scary_intro2")

        vvSplash.setVideoURI(intro)
        vvSplash.start()*/

    }
}