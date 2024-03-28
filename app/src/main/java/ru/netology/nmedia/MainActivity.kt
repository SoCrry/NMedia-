package ru.netology.nmedia


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
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
        with(binding) {
            author.text = post.author
            content.text = post.content
            timePublication.text = post.published
            like.text = countWithSuffix(post.like)
            shared.text = countWithSuffix(post.shared)
            viewers.text = countWithSuffix(post.views)
            if (post.likeByMe) {
                likesIv.setImageResource(R.drawable.liked)
            }
            sharedIv.setOnClickListener {
                post.shared += 10
                shared.text = countWithSuffix(post.shared)
            }

            likesIv.setOnClickListener {
                post.likeByMe = !post.likeByMe
                likesIv.setImageResource(if (post.likeByMe) R.drawable.liked else R.drawable.likes)
                if (post.likeByMe) post.like++ else post.like--
                like.text = countWithSuffix(post.like)
            }
        }

    }

    private fun countWithSuffix(count: Int): String {
        val value = count.toDouble()
        val suffixChars = "KMGTPE"
        val formatter = DecimalFormat("##.#")
        formatter.roundingMode = RoundingMode.DOWN
        return if (value < 1000.0) formatter.format(value)
        else {
            val exp = (ln(value) / ln(1000.0)).toInt()
            val preFormat = value / 1000.0.pow(exp.toDouble())
            if (preFormat >= 10.0 || preFormat == 1.0) {
                preFormat.toInt().toString() + suffixChars[exp - 1]
            } else {
                formatter.format(preFormat) + suffixChars[exp - 1]
            }
        }

    }
}