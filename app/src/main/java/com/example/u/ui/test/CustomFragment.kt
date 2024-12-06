package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.u.R
import com.example.u.databinding.FragmentCustomBinding

class CustomFragment : Fragment() {

    private var _binding: FragmentCustomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCustomBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Glide.with(requireContext())
            .load(requireContext().getDrawable(R.drawable.bg_test_code))  // 指定图片 URL 或资源 ID
            .circleCrop()
            .into( binding.ivGlide)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}