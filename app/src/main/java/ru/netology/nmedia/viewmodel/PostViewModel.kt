package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository

import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0,
    views = 0,
    shared = 0
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val old = _data.value?.posts.orEmpty()

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.NMediaCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
    fun save() {
        edited.value?.let {
            repository.save(it, object  : PostRepository.NMediaCallback<Post>{
                override fun onSuccess(posts: Post) {
                    _postCreated.postValue(Unit)
                }
                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(error = true))
                }
            })
        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun edit(post: Post) {
       thread{
           edited.postValue(post)
       }

    }

    fun setEmptyPost() {
        edited.value = empty
    }
    fun sharedById(id: Long) = repository.sharedById(id)

    fun likeById(id: Long) {
        repository.likeById(id, object : PostRepository.NMediaCallback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(error = true))
            }

            override fun onSuccess(posts: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) posts else it
                        }
                    )
                )
            }
        })
    }

    fun unLikeById(id: Long) {
        repository.unLikeById(id, object : PostRepository.NMediaCallback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(error = true))
            }

            override fun onSuccess(posts: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) posts else it
                        }
                    )
                )
            }
        })
    }

    fun removeById(id: Long) {
        _data.value = _data.value?.copy(posts = _data.value?.posts.orEmpty()
            .filter { it.id != id }
        )
        repository.removeById(id, object : PostRepository.NMediaCallback<Unit> {
            override fun onSuccess(data: Unit) {
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(error = true))
            }
        })
    }

    fun getVideoUri(post: Post): Uri {
        return Uri.parse(post.urlVideo)
    }
    fun getAvatarUrl(fileName: String) {
        repository.getAvatarUrl(fileName)
    }

}
