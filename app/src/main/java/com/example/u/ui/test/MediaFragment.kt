package com.example.u.ui.test

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import com.example.u.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {

    private lateinit var exoPlayer: ExoPlayer
    private var _binding: FragmentMediaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(MediaViewModel::class.java)

        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initPlayer()

        return root
    }

    @OptIn(markerClass = [UnstableApi::class])
    private fun setRenderersFactory(
        playerBuilder: ExoPlayer.Builder
    ) {
        val mode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        val renderersFactory: RenderersFactory =
            DefaultRenderersFactory(requireContext()).setExtensionRendererMode(mode)
        playerBuilder.setRenderersFactory(renderersFactory)
    }

    private fun initializeVideo() {
        val playerBuilder = ExoPlayer.Builder(requireContext())
        setRenderersFactory(playerBuilder)
        exoPlayer = playerBuilder.build()
        exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
        exoPlayer.playWhenReady = false

        binding.playerView.setPlayer(exoPlayer)

        val uri = Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4")
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }


    @OptIn(UnstableApi::class)
    private fun initPlayer() {
        initializeVideo()
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
            }

            override fun onPlayerError(error: PlaybackException) {
                exoPlayer.prepare()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                    // 播放完成
                } else {
                }
            }
        })

        // 3s后开始清屏
        binding.playerView.setControllerShowTimeoutMs(3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        // 在 onPause 时暂停播放
        exoPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        // 在 onStop 时释放播放器资源
        exoPlayer.release()
    }

    private fun setVideoPlaySpeed(speed: String) {
        val playbackParameters =
            when (speed) {
                "2.0x" -> PlaybackParameters(2.0f, 1.0f)
                "0.75x" -> PlaybackParameters(0.75f, 1.0f)
                "0.5x" -> PlaybackParameters(0.5f, 1.0f)
                "1.0x" -> PlaybackParameters(1.0f, 1.0f)
                else -> PlaybackParameters(1.0f, 1.0f)
            }
        exoPlayer.playbackParameters = playbackParameters
    }
}

