package tech.gamedev.scared.ui.fragments.main5

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import tech.gamedev.scared.R
import tech.gamedev.scared.databinding.FragmentProfileBinding
import tech.gamedev.scared.other.Constants.AUTH_REQUEST_CODE
import tech.gamedev.scared.ui.viewmodels.LoginViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var auth: FirebaseAuth
    private var signInVisible = false


    private lateinit var binding: FragmentProfileBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.tvPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_privacyPolicyFragment)
        }

        binding.btnLogin.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, AUTH_REQUEST_CODE)
        }

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
        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.clProfileSettings.isVisible = true
                binding.tvName.text = it.displayName
                binding.tvNameMain.text = it.displayName
                glide.load(it.photoUrl).into(binding.ivProfileImg)
                glide.load(it.photoUrl).into(binding.ivProfileImgMain)

                binding.btnLogin.isVisible = false
                binding.tvNotLoggedIn.isVisible = false
            } else {
                binding.clProfileSettings.isVisible = false
                binding.tvNotLoggedIn.isVisible = true
                binding.btnLogin.isVisible = true
                binding.ivProfileImg.setImageResource(R.drawable.ic_account)
                binding.tvName.text = getString(R.string.not_signed_in)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.tag(ContentValues.TAG).w(e, "Google sign in failed")
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("signInWithCredential:success")
                        val user = auth.currentUser
                        if(user != null) {
                            loginViewModel.assignUser(user)
                        }
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.tag(ContentValues.TAG).w(task.exception, "signInWithCredential:failure")
                        // ...
                        Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                                .show()
                        updateUI(null)
                    }
                }
    }

    private fun updateUI(acct: FirebaseUser?) {
        if (acct != null) {
            //CHECK IF USER EXISTS AND IF NOT ADD TO USER DATABASE
            loginViewModel.checkIfUserExists(acct)
        } else {
            Timber.d("NOT LOGGED IN YET")
        }
    }
}