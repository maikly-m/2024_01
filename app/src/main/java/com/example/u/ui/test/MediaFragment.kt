package com.example.u.ui.test

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import com.example.u.databinding.FragmentMediaBinding
import com.example.u.uitls.DisplayUtils
import timber.log.Timber

class MediaFragment : Fragment() {

    val mUri = Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4")
    private lateinit var mPlayer: ExoPlayer
    private var _binding: FragmentMediaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mGestureDetector: GestureDetector? = null
    private var mNetworkCallback: ConnectivityManager.NetworkCallback? = null
    private var mCurrentPosition: Long = 0
    private var mInitPlay = false

    private var mIsPause = false
    private var mIsBgPlaying = false

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


        //使用 texture_view， 然后裁一下圆角(只要裁剪上面)
        binding.playerView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val radius: Int = DisplayUtils.dp2px(requireContext(), 16f)
                outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
        binding.playerView.setClipToOutline(true)

        initData()

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
        mPlayer = playerBuilder.build()
        binding.playerView.player = mPlayer
        mPlayer.setAudioAttributes(AudioAttributes.DEFAULT,  /* handleAudioFocus= */true)
        val mediaItem = MediaItem.fromUri(mUri)
        mPlayer.setMediaItem(mediaItem)
        mPlayer.prepare()
        mPlayer.playWhenReady = true
    }

    private fun initData() {
        // Listening network change
        val manager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Timber.d("NetworkCallback onAvailable")
                if (!mInitPlay) {
                    mInitPlay = true
                    binding.playerView.post {
                        initializeVideo()
                        initListener()
                    }
                } else {
                    binding.playerView.post {
                        mPlayer.prepare()
                        mPlayer.playWhenReady = true
                        mPlayer.seekTo(mCurrentPosition)
                        mPlayer.pause()
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                mCurrentPosition = mPlayer.currentPosition
            }
        }
        manager.registerNetworkCallback(NetworkRequest.Builder().build(), mNetworkCallback!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mGestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(event: MotionEvent): Boolean {
                    //双击暂停或者播放
                    controllerVideo()
                    return true
                }
            })
        binding.playerView.setOnTouchListener { v, event -> mGestureDetector!!.onTouchEvent(event) }

        //监听播放状态

        // 监听播放状态
        mPlayer.addListener(object : Player.Listener {

            @OptIn(UnstableApi::class)
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    // 媒体源已准备好，可以播放
                    binding.playerView.showController()
                } else if (state == Player.STATE_IDLE) {
                    // 播放器处于空闲状态或正在缓冲
                    // 其他状态，可能是错误状态
                    binding.playerView.showController()
                }

//                if (state == Player.STATE_ENDED) {
//                    // 播放完毕，返回到初始播放位置
//                    mPlayer.seekTo(0)
//                    mPlayer.pause()
//                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                mCurrentPosition = newPosition.positionMs
            }
        })
        // 无限循环
        // mPlayer.repeatMode = Player.REPEAT_MODE_ALL
    }

    @OptIn(UnstableApi::class)
    private fun controllerVideo() {
        if (mPlayer.playbackState == Player.STATE_ENDED) {
            mPlayer.seekTo(mPlayer.currentMediaItemIndex, C.TIME_UNSET)
        } else if (mPlayer.isPlaying) {
            mPlayer.pause()
        } else {
            mPlayer.play()
        }
    }

    override fun onResume() {
        if (mIsPause && mIsBgPlaying) {
            // retrieve play state
            mPlayer.play()
        }
        mIsPause = false
        super.onResume()
    }

    override fun onPause() {
        mIsPause = true
        mIsBgPlaying = mPlayer.isPlaying
        mPlayer.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mPlayer.release()
        val manager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.unregisterNetworkCallback(mNetworkCallback!!)
    }

}

