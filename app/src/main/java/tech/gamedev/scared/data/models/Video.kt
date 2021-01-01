package tech.gamedev.scared.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Video(
    val url: String,
    val title: String,
    val description: String,
    val likes: Int
) {
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}