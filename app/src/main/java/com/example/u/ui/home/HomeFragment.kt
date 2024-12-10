package com.example.u.ui.home

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.u.R
import com.example.u.databinding.FragmentHomeBinding
import timber.log.Timber

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var backPressedTime: Long = 0
    private val doubleBackToExitInterval: Long = 2000 // 2秒

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnPermission.setOnClickListener {
            findNavController().navigate(R.id.home2permission)
        }
        binding.btnRichText.setOnClickListener {
            findNavController().navigate(R.id.home2rich_text)
        }
        binding.btnTextTest.setOnClickListener {
            findNavController().navigate(R.id.home2text_test)
        }
        binding.btnCamera.setOnClickListener {
            findNavController().navigate(R.id.home2camera)
        }
        binding.btnRecycleView.setOnClickListener {
            findNavController().navigate(R.id.home2recycle_view)
        }
        binding.btnCustomView.setOnClickListener {
            findNavController().navigate(R.id.home2custom_view)
        }
        binding.btnLoadMore.setOnClickListener {
            findNavController().navigate(R.id.home2load_more)
        }
        binding.btnNetTest.setOnClickListener {
            findNavController().navigate(R.id.home2net_test)
        }
        binding.btnDownloadAndInstall.setOnClickListener {
            findNavController().navigate(R.id.home2download_and_install)
        }
        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.home2slide_verify)
        }
        binding.btnMedia.setOnClickListener {
            findNavController().navigate(R.id.home2media)
        }


        // 监听返回键操作
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (System.currentTimeMillis() - backPressedTime < doubleBackToExitInterval) {
                // 双击返回键，退出应用
                requireActivity().finish()
            } else {
                // 单击返回键，显示提示 Toast
                Toast.makeText(requireContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }

        setFragmentResultListener("test"){ key, bundle ->
            Timber.d("setFragmentResultListener key=${key}")
            Timber.d("setFragmentResultListener bundle key=${bundle.getString("key")}")
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun togglePasswordEditTextVisibility(editText: EditText) {
        val textPassword = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        val textVisiblePassword = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        editText.inputType = if (editText.inputType == textPassword) {
            textVisiblePassword
        } else {
            textPassword
        }
    }

}