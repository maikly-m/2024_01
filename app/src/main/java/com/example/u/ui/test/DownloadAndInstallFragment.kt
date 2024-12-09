package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.u.R
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

        binding.btnHome.setOnClickListener {
            // 获取 NavController
            val navController = findNavController()
            navController.popBackStack(R.id.navigation_home, false)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true) // 清除栈，返回到首页
                .build()
            navController.navigate(R.id.home2load_more, null, navOptions)
//            // 创建导航选项，清除栈并返回首页
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(R.id.navigation_home, true) // 清除栈，返回到首页
//                .build()
//            navController.navigate(R.id.download_and_install2home, null, navOptions)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}