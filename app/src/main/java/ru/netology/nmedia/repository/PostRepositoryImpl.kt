package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.Post
import ru.netology.nmedia.PostEntity
import ru.netology.nmedia.dao.PostDao

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {
    override fun getAll() = Transformations.map(dao.getAll()){list ->
        list.map {
            Post(it.id,it.author,it.content,it.published,it.likesAmount,it.sharesAmount,it.likedByMe,it.views,it.attachedVideo)
        }
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}