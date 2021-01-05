package tech.gamedev.scared.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import tech.gamedev.scared.data.models.Story
import tech.gamedev.scared.other.Constants.STORY_COLLECTION

class FirebaseDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val storyCollection = firestore.collection(STORY_COLLECTION)
    private val documentaryCollection =
        firestore.collection("videos").document("documentaries").collection("2020")
    private val redditCollection =
        firestore.collection("videos").document("reddit").collection("2020")

    suspend fun getAllStories(): List<Story> {
        return try {
            storyCollection.get().await().toObjects(Story::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getStoryList(): Task<QuerySnapshot> {
        return storyCollection
            .get()
    }

    fun getDocumentaryList(): Task<QuerySnapshot> {
        return documentaryCollection
            .get()
    }

    fun getRedditList(): Task<QuerySnapshot> {
        return redditCollection
            .get()
    }
}