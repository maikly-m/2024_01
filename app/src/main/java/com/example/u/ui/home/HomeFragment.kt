package com.example.u.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.R
import com.example.u.databinding.FragmentHomeBinding
import com.permissionx.guolindev.PermissionX

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnPermission.setOnClickListener {
            PermissionX.init(requireActivity())
                .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
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
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "The following permissions are denied：$deniedList", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }

    fun grantExternalStorage(): Unit {
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
        if (permissions.size > 0){
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
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "The following permissions are denied：$deniedList", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}