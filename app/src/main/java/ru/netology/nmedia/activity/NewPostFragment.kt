package ru.netology.nmedia.activity


import android.app.Notification.EXTRA_TEXT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.content.focusAndShowKeyboard()
        arguments?.textArg
            ?.let{
                    val text = it
                    binding.content.setText(text)
                    binding.titleEdit.visibility = View.VISIBLE
                }
        viewModel.setEmptyPost()
        binding.save.setOnClickListener {
            viewModel.changeContentAndSave(binding.content.text.toString())
            AndroidUtils.hideKeyboard(requireView())
            viewModel.setEmptyPost()
            findNavController().navigateUp()
        }

        binding.cancel.setOnClickListener() {
            viewModel.setEmptyPost()
            findNavController().navigateUp()
        }
       return binding.root
    }
}

object NewPostContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?) =
        Intent(context, NewPostFragment::class.java).putExtra(
            EXTRA_TEXT, input
        )

    override fun parseResult(resultCode: Int, intent: Intent?) =
        intent?.getStringExtra(Intent.EXTRA_TEXT)
}



