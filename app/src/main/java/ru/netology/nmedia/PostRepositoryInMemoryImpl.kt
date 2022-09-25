package ru.netology.nmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:56",
            likesAmount = 999,
            sharesAmount = 9_995,
            likedByMe = false,
            views = 12_232_342
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Тут какой-то новый текст, но на слайде я вижу лишь часть текста и не знаю что будет дальше в посте в презентации, может быть оно есть в репозитории данных для домашнего задания, но лень копатся там ради одного текстового поля, так что хватит здесь и этого.",
            published = "18 мартобря в 10:26",
            likesAmount = 10_000,
            sharesAmount = 999_997,
            likedByMe = true,
            views = 1_232_342
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. Еще один тест просточтобы заполнить место,чтобы растянуть список постов вниз и проверить как хорошо работает скроллинг и RecyclerView. ",
            published = "18 феврония в 10:26",
            likesAmount = 156,
            sharesAmount = 99_991,
            likedByMe = true,
            views = 12_342
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.Зачем делать такие длинные телефоны, скроллишь и скроллишь вниз, все для тестовых записей.",
            published = "15 дудониста в 11:22",
            likesAmount = 12,
            sharesAmount = 1_198,
            likedByMe = false,
            views = 1123
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likesAmount = if (it.likedByMe) it.likesAmount - 1 else it.likesAmount + 1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesAmount = it.sharesAmount + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}