package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.FragmentPhotoViewBinding


class PhotoView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPhotoViewBinding.inflate(inflater, container, false)

        val imageUrl = arguments?.getString("imageUrl")


        binding.cancel.setOnClickListener {
            findNavController().navigateUp()
        }

        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.fullScreenImageView)
        }

        return binding.root
    }
}