package tech.gamedev.scared.repo

import tech.gamedev.scared.data.remote.FirebaseDatabase
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
){

    fun getAllStories() = firebaseDatabase.getStoryList()

    fun getAllDocumentaries() = firebaseDatabase.getDocumentaryList()

    fun getAllRedditVideos() = firebaseDatabase.getRedditList()

}