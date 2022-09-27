package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    published = "",

)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun empty(){
        edited.value= empty
    }

    fun save(){
        edited.value?.let {
            repository.save(it)
        }
        empty()
    }

    fun edit(post: Post){
        edited.value = post
    }

    fun changeContent(content: String){
        edited.value?.let{
            val text = content.trim()
            if(it.content == text){
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}