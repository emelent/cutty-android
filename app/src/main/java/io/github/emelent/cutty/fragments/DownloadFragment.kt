package io.github.emelent.cutty.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cutty.R
import com.example.cutty.databinding.DownloadDataBinding
import com.example.cutty.databinding.EnterUrlDataBinding
import io.github.emelent.cutty.SharedViewModel
import kotlinx.android.synthetic.main.fragment_download.*

class DownloadFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var downloadUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_download, container, false)
        val binding = DownloadDataBinding.bind(root)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.hubConnection.on("DownloadLink", { downloadLink: String ->
            requireActivity().runOnUiThread {
                sharedViewModel.loading.value =false
                downloadUrl = downloadLink
                v_download.visibility = View.VISIBLE
                v_message.text = "Video ready"
                println("Player::hub::downloadLink -> $downloadLink")
            }
        }, String::class.java)

        sharedViewModel.hubConnection.on("ReceiveMessage", { message: String ->
            println("Player::hub::message -> $message")
            requireActivity().runOnUiThread {
                v_message.text = message
            }
        }, String::class.java)

        v_download.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(downloadUrl)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.hubConnection.remove("AvUrls")
    }
}