package tech.gamedev.scared.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import tech.gamedev.scared.data.models.Song
import tech.gamedev.scared.data.models.Story
import tech.gamedev.scared.other.Constants.AUDIO_COLLECTION
import tech.gamedev.scared.other.Constants.STORY_COLLECTION
import java.lang.Exception

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(AUDIO_COLLECTION)


    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }


}