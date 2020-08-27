package io.github.emelent.cutty.fragments

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cutty.R
import io.github.emelent.cutty.SharedViewModel
import com.example.cutty.databinding.EnterUrlDataBinding
import kotlinx.android.synthetic.main.fragment_enter_url.*

class EnterUrlFragment: Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_enter_url, container, false)
        val binding = EnterUrlDataBinding.bind(root)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        v_url_container.setEndIconOnClickListener {
            v_url.setText(clipboard.primaryClip?.getItemAt(0)?.text)
        }
        v_go.setOnClickListener {
           sharedViewModel.requestAvUrl(v_url.text.toString())
        }

        // observations
        sharedViewModel.hubConnection.on("AvUrls", { videoUrl: String, audioUrl: String ->
            println("Player::EnterUrl::Audio url -> $audioUrl")
            println("Player::EnterUrl::Video url -> $videoUrl")
            sharedViewModel.loading.postValue(false)
            sharedViewModel.goToVideoTrimmer(audioUrl, videoUrl)
        }, String::class.java, String::class.java)



        requireActivity().intent.getStringExtra(Intent.EXTRA_TEXT)?.let{
           v_url.setText(it)
            sharedViewModel.requestAvUrl(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.hubConnection.remove("AvUrls")
    }
}