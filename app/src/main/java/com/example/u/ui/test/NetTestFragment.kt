package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.databinding.FragmentNetTestBinding

class NetTestFragment : Fragment() {

    private var _binding: FragmentNetTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(NetTestViewModel::class.java)

        _binding = FragmentNetTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textNotifications.setOnClickListener {
            viewModel.getBanner()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}