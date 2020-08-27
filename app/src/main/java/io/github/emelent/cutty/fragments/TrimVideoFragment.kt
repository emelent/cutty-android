package io.github.emelent.cutty.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.cutty.R
import io.github.emelent.cutty.SharedViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.slider.RangeSlider
import io.github.emelent.cutty.toHhMmSs
import kotlinx.android.synthetic.main.fragment_trim_video.*

class TrimVideoFragment : Fragment(R.layout.fragment_trim_video) {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val navArgs by navArgs<TrimVideoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val player = SimpleExoPlayer.Builder(requireContext()).build()
        v_player.player = player

        val mediaSourceFactory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(
                requireContext(),
                getString(R.string.app_name)
            )
        ).let {
            ProgressiveMediaSource.Factory(it)
        }

        val audioSource = mediaSourceFactory.createMediaSource(Uri.parse(navArgs.audioUrl))
        val videoSource = mediaSourceFactory.createMediaSource(Uri.parse(navArgs.videoUrl))
        val mergedSource = MergingMediaSource(videoSource, audioSource)
        player.prepare(mergedSource)

        var startPos = v_slider.values[0]
        var endPos = v_slider.values[1]

        v_slider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                startPos = slider.values[0]
                endPos = slider.values[1]
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val newStartPos = slider.values[0]
                val newEndPos = slider.values[1]
                val seekStart = (player.duration * (newStartPos / 100.0)).toLong()
                val seekEnd = (player.duration * (newEndPos / 100.0)).toLong()
                if (newStartPos != startPos) {
                    player.seekTo(seekStart)
                }
                if (newEndPos != endPos) {
                    player.seekTo(seekEnd)
                }

                sharedViewModel.seekPos = Pair(seekStart, seekEnd)
            }
        })
        v_slider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val newStartPos = slider.values[0]
                val newEndPos = slider.values[1]

                val seekStart = (player.duration * (newStartPos / 100.0)).toLong()
                val seekEnd = (player.duration * (newEndPos / 100.0)).toLong()
                v_duration.text = (seekEnd - seekStart).toHhMmSs()
            }
        }


        v_trim.setOnClickListener { sharedViewModel.requestTrimVideo() }
    }
}