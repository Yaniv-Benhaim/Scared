package tech.gamedev.scared.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import tech.gamedev.scared.R
import tech.gamedev.scared.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    private lateinit var binding: FragmentPrivacyPolicyBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPrivacyPolicyBinding.bind(view)

        binding.wvPrivacyPolicy.apply {

        }

        val webView = binding.wvPrivacyPolicy

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.loadUrl(getString(R.string.website_url))
        WebView.setWebContentsDebuggingEnabled(false)
    }


}


