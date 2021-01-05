package tech.gamedev.scared.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.gamedev.scared.data.models.Video
import tech.gamedev.scared.repo.MainRepository

class VideoViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _documentaries = MutableLiveData<ArrayList<Video>>()
    val documentaries: LiveData<ArrayList<Video>> = _documentaries

    private val _redditVideos = MutableLiveData<ArrayList<Video>>()
    val redditVideos: LiveData<ArrayList<Video>> = _redditVideos

    private val _allVideos = MutableLiveData<ArrayList<Video>>()
    val allVideos: LiveData<ArrayList<Video>> = _allVideos

    private val _isFullScreen = MutableLiveData<Boolean>()
    val isFullScreen: LiveData<Boolean> = _isFullScreen

    init {
        getAllDocumentaries()
        getAllRedditVideos()

    }

    fun setIsFullScreen(fullScreen: Boolean) {
        _isFullScreen.value = fullScreen
    }

    private fun getAllDocumentaries() = viewModelScope.launch {
        _documentaries.value = ArrayList()
        _allVideos.value = ArrayList()
        mainRepository.getAllDocumentaries().addOnCompleteListener {

            if (it.isSuccessful) {

                for (document in it.result!!) {
                    val docu = document.toObject(Video::class.java)
                    _documentaries.value!!.add(docu)
                    _allVideos.value!!.add(docu)
                }
            }
            addAllVideosToShuffledList()
        }
    }

    private fun getAllRedditVideos() = viewModelScope.launch {
        _redditVideos.value = ArrayList()
        mainRepository.getAllRedditVideos().addOnCompleteListener {

            if (it.isSuccessful) {

                for (document in it.result!!) {
                    val reddit = document.toObject(Video::class.java)
                    _redditVideos.value!!.add(reddit)
                    _allVideos.value!!.add(reddit)
                }
                addAllVideosToShuffledList()
            }
        }
    }

    private fun addAllVideosToShuffledList() {
        if (_allVideos.value.isNullOrEmpty()) {
            _allVideos.value = ArrayList()
            _allVideos.value!!.addAll(_redditVideos.value!!)
        } else {
            _allVideos.value!!.shuffle()
        }


    }
}