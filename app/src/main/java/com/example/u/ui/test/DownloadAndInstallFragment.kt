package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.databinding.FragmentDownloadAndInstallBinding
import com.example.u.uitls.Installer
import java.io.File

class DownloadAndInstallFragment : Fragment() {

    private var _binding: FragmentDownloadAndInstallBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(DownloadAndInstallViewModel::class.java)

        _binding = FragmentDownloadAndInstallBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.tvTest.setOnClickListener {
//            Installer.downloadAndInstallApk(requireContext(),"", "test.apk")
//        }
        binding.tvTest.setOnClickListener {
            Installer.installApk(requireContext(),  File(context?.cacheDir, "cfa-2.5.12-premium-universal-release.apk"))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}