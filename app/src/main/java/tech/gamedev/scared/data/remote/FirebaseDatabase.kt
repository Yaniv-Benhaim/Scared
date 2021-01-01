package tech.gamedev.scared.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import tech.gamedev.scared.data.models.Story
import tech.gamedev.scared.other.Constants
import tech.gamedev.scared.other.Constants.STORY_COLLECTION
import java.lang.Exception

class FirebaseDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val storyCollection = firestore.collection(STORY_COLLECTION)

    suspend fun getAllStories(): List<Story> {
        return try {
            storyCollection.get().await().toObjects(Story::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }

    fun getStoryList(): Task<QuerySnapshot> {
        return storyCollection
            .get()
    }
}