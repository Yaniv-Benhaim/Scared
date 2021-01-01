package tech.gamedev.scared.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import tech.gamedev.scared.data.remote.FirebaseDatabase
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
){

    fun getAllStories() = firebaseDatabase.getStoryList()

}