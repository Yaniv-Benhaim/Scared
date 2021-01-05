package tech.gamedev.scared.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import tech.gamedev.scared.repo.LoginRepository


class LoginViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {



    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser> = _user

    fun assignUser(user: FirebaseUser) {
        _user.value = user
    }


    fun checkIfUserExists(acct: FirebaseUser?) = viewModelScope.launch {
        loginRepository.checkIfUserExists(acct)
    }

    fun signOut() {
        _user.value = null
    }


}