package com.example.u.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.u.R
import com.example.u.camera.CameraActivity
import com.example.u.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView(dashboardViewModel)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView(dashboardViewModel: DashboardViewModel) {
        binding.btnToast.setOnClickListener {
            lifecycle.coroutineScope.launch(Dispatchers.IO) {
                dashboardViewModel.setText("test")
            }
        }
        binding.btnRequest.setOnClickListener {
            dashboardViewModel.getTextFlow()
        }
        binding.btnCamera.setOnClickListener {
            Intent(requireActivity(), CameraActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnGallery.setOnClickListener {
            openGallery()
        }
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        dashboardViewModel.textFlow.observe(viewLifecycleOwner) {
            if (TextUtils.isEmpty(it)) {
                return@observe
            }
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    // 获取系统相册图片
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // 例如，你可以将选中的图片显示到 ImageView 中
            binding.imgGallery.setImageURI(uri)
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }


}