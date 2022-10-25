package ru.netology.nmedia

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likesAmount: Long = 0,
    val sharesAmount: Long = 0,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val attachedVideo: String = ""
){
    fun toDto() = Post(id,author,content, published, likesAmount, sharesAmount, likedByMe, views, attachedVideo)

    companion object{
        fun fromDto(dto:Post) =
            PostEntity(dto.id,dto.author,dto.content,dto.published,dto.likesAmount,dto.sharesAmount,dto.likedByMe,dto.views,dto.attachedVideo)
    }
}
