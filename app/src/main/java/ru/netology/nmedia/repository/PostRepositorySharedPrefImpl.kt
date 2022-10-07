package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.mockito.internal.matchers.Null
import ru.netology.nmedia.Post

class PostRepositorySharedPrefImpl (context: Context): PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo",Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key,null)?.let {
            posts = gson.fromJson(it,type)
            nextId = (posts.maxOfOrNull { it.id }?:0)+1
        } ?: run{
            posts = listOf(
            Post(
                id = nextId++,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                published = "21 мая в 18:56",
                likesAmount = 999,
                sharesAmount = 9_995,
                likedByMe = false,
                views = 12_232_342
            ),
            Post(
                id = nextId++,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Тут какой-то новый текст, но на слайде я вижу лишь часть текста и не знаю что будет дальше в посте в презентации, может быть оно есть в репозитории данных для домашнего задания, но лень копатся там ради одного текстового поля, так что хватит здесь и этого.",
                published = "18 мартобря в 10:26",
                likesAmount = 10_000,
                sharesAmount = 999_997,
                likedByMe = true,
                views = 1_232_342,
                attachedVideo = "https://www.youtube.com/watch?v=3m7ZUL8zJSc"
            ),
            Post(
                id = nextId++,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. ",
                published = "18 феврония в 10:26",
                likesAmount = 156,
                sharesAmount = 99_991,
                likedByMe = true,
                views = 12_342
            ),
            Post(
                id = nextId++,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.",
                published = "15 дудониста в 11:22",
                likesAmount = 12,
                sharesAmount = 1_198,
                likedByMe = false,
                views = 1123,
                attachedVideo = "https://www.youtube.com/watch?v=JDXfqN3VKhc"
            )
        )
        }
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId++,
                    author = "me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
        } else {
            posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
        sync()
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likesAmount = if (it.likedByMe) it.likesAmount - 1 else it.likesAmount + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesAmount = it.sharesAmount + 1)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    private fun sync(){
        prefs.edit().apply{
            putString(key,gson.toJson(posts))
            apply()
        }
    }
}