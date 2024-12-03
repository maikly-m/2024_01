package com.example.u.ui.test

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.u.databinding.FragmentPermissionBinding
import com.example.u.uitls.DisplayUtils
import com.permissionx.guolindev.PermissionX
import timber.log.Timber


class PermissionFragment : Fragment() {

    private var _binding: FragmentPermissionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(PermissionViewModel::class.java)

        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Timber.d("onCreateView ")

        binding.header.headerTitle.text = "Permission"

        viewModel.text.observe(viewLifecycleOwner){
            Timber.d("PermissionFragment text $it")
        }
        viewModel.text2.observe(viewLifecycleOwner){
            Timber.d("PermissionFragment text2 $it")
        }
        viewModel.text3.observe(viewLifecycleOwner){
            Timber.d("PermissionFragment text3 $it")
        }
        viewModel.text4.observe(viewLifecycleOwner){
            Timber.d("PermissionFragment text4 $it")
        }

        binding.btnTestState.setOnClickListener {
            viewModel.testFlow()
            binding.btnTest.postDelayed({
                findNavController().navigate(com.example.u.R.id.permission2custom_view)
            }, 200)

        }
        binding.btnTestState2.setOnClickListener {
            findNavController().navigate(com.example.u.R.id.permission2custom_view)
        }

        // 加载图片为 Bitmap
        val bitmap = BitmapFactory.decodeResource(resources, com.example.u.R.drawable.bg_test_code)
        // 创建圆角 Drawable
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedBitmapDrawable.cornerRadius = DisplayUtils.dp2px(context, 10f).toFloat() // 设置圆角半径，可以调整
        // 设置圆角图片到 ImageView
        binding.ivTestRound.setImageDrawable(roundedBitmapDrawable)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTest.setOnClickListener {
            PermissionX.init(requireActivity())
                .permissions(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.CALL_PHONE
                )
                .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
                .onExplainRequestReason { scope, deniedList, beforeRequest ->
                    val message = "PermissionX needs following permissions to continue"
                    scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
//                    val message = "Please allow the following permissions in settings"
//                    val dialog = CustomDialogFragment(message, deniedList)
//                    scope.showRequestReasonDialog(dialog)
                }
                .onForwardToSettings { scope, deniedList ->
                    val message = "Please allow following permissions in settings"
                    scope.showForwardToSettingsDialog(deniedList, message, "Allow", "Deny")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            activity,
                            "The following permissions are denied：$deniedList",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.btnStorage.setOnClickListener {
            grantExternalStorage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun grantExternalStorage(): Unit {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // android 13及其以上版本
            if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_DENIED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ||
                requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        if (permissions.size > 0) {
            PermissionX.init(requireActivity())
                .permissions(permissions)
                .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
                .onExplainRequestReason { scope, deniedList, beforeRequest ->
                    val message = "PermissionX needs following permissions to continue"
                    scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
                }
                .onForwardToSettings { scope, deniedList ->
                    val message = "Please allow following permissions in settings"
                    scope.showForwardToSettingsDialog(deniedList, message, "Allow", "Deny")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            activity,
                            "The following permissions are denied：$deniedList",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}