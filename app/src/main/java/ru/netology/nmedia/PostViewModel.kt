package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "Me",
    authorAvatar = "",
    published = "",
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    val edited = MutableLiveData(empty)
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = repository.data.map {
            FeedModel(it,it.isEmpty())
        }
    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<String>()
    val postCreatedError: LiveData<String>
        get() = _postCreatedError
    private val _postsEditError = SingleLiveEvent<String>()
    val postsEditError: LiveData<String>
        get() = _postsEditError

    var draft = ""

    init {
        load()
    }

    fun load(isRefreshing: Boolean = false) = viewModelScope.launch {
        _dataState.value = if(isRefreshing) FeedModelState.Refreshing else FeedModelState.Loading
        try {
            repository.getAll()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception){
            _dataState.value = FeedModelState.Error
        }
    }

    fun empty() {
        edited.value = empty
    }

    fun save() = viewModelScope.launch{
        edited.value?.let {
            repository.save(it)
            _postCreated.postValue(Unit)
            //_postCreated.value = Unit
        }
        empty()
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun likeById(id: Long, likedByMe: Boolean) = viewModelScope.launch {
        repository.likeById(id, !likedByMe, )
    }

    fun shareById(id: Long) = viewModelScope.launch {  repository.shareById(id)}

    fun removeById(id: Long) = viewModelScope.launch {
        val old = _data.value
        val filtered = old?.posts.orEmpty().filter { it.id != id }
        _data.postValue(old?.copy(posts = filtered, empty = filtered.isEmpty()))

        repository.removeById(id, )
    }
}