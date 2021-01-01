package tech.gamedev.scared.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.gamedev.scared.data.models.Story
import tech.gamedev.scared.repo.MainRepository
import javax.inject.Inject

class StoryViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>()
    val stories: LiveData<ArrayList<Story>> = _stories

    init {
        getAllStories()
    }

    private fun getAllStories() = viewModelScope.launch {


        _stories.value = ArrayList()
        mainRepository.getAllStories().addOnCompleteListener {

            if(it.isSuccessful) {
                for (document in it.result!!){
                    val story = document.toObject(Story::class.java)
                    _stories.value!!.add(story)
                }
            }

        }
    }
}