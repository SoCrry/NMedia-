package ru.netology.nmedia


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val  binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post =Post(1,
            "Нетология.Университет интернет-профессий",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.\n" +
                  " Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb\" </string>\n ",
            "21 мая 18:36",
            false,
            10000,
            1000,
            15000)
        with(binding){
            author.text=post.author
            content.text=post.content
            timePublication.text=post.published
            like.text= post.like.toString()
            shared.text= post.shared.toString()
            viewers.text= post.views.toString()

            if (post.likeByMe){
                likesIv?.setImageResource(R.drawable.liked)
            }

            likesIv?.setOnClickListener{
                post.likeByMe=!post.likeByMe
                likesIv.setImageResource(if (post.likeByMe) R.drawable.liked else R.drawable.likes)
            }
        }

    }
}