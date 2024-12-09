package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.u.databinding.FragmentSlideVerifyBinding
import com.slide.verify.SlideVerifyView


class SlideVerifyFragment : Fragment() {

    private var _binding: FragmentSlideVerifyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(SlideVerifyViewModel::class.java)

        _binding = FragmentSlideVerifyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val captcha = binding.captCha
        val btnMode = binding.btnMode
        btnMode.setOnClickListener(View.OnClickListener {
            if (captcha.mode == SlideVerifyView.MODE_BAR) {
                captcha.setMode(SlideVerifyView.MODE_NO_BAR)
                btnMode.text = "滑动条模式"
            } else {
                captcha.setMode(SlideVerifyView.MODE_BAR)
                btnMode.text = "无滑动条模式"
            }
        })
        captcha.setVerifyListener(object : SlideVerifyView.CaptchaListener {
            override fun onAccess(time: Long): String {
                Toast.makeText(requireContext(), "验证成功", Toast.LENGTH_SHORT).show()
                return "验证通过"
            }

            override fun onFailed(count: Int): String {
                Toast.makeText(
                    requireContext(),
                    "验证失败,失败次数$count", Toast.LENGTH_SHORT
                ).show()
                return "验证失败"
            }

            override fun onMaxFailed(): String {
                Toast.makeText(requireContext(), "验证超过次数，你的帐号被封锁", Toast.LENGTH_SHORT)
                    .show()
                return "可以走了"
            }
        })

        setFragmentResult("test",  Bundle().apply {
            putString("key", "ha")
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}