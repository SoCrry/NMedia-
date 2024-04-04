package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        1,
        "Нетология.Университет интернет-профессий",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.\n" +
                " Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb\" </string>\n ",
        "21 мая 18:36",
        false,
        10,
        1000,
        15000
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        post = post.copy(likeByMe = !post.likeByMe, like = post.like + if (post.likeByMe) -1 else 1)
        data.value = post
    }

    override fun shared() {
        post = post.copy(shared = post.shared + 10)

        data.value = post
    }
}