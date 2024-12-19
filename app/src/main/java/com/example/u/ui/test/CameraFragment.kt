package com.example.u.ui.test

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.u.camera.CameraActivity
import com.example.u.databinding.FragmentCameraBinding
import com.example.u.scan_code.ScanTestActivity
import com.example.u.scan_code.mutli.CameraXScanActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class CameraFragment : Fragment() {

    private var photoFile: File? = null
    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView(viewModel)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView(model: CameraViewModel) {
        binding.btnToast.setOnClickListener {
            lifecycle.coroutineScope.launch(Dispatchers.IO) {
                model.setText("test")
            }
        }
        binding.btnRequest.setOnClickListener {
            model.getTextFlow()
        }
        binding.btnCamera.setOnClickListener {
            Intent(requireActivity(), CameraActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnGallery.setOnClickListener {
            openGallery()
        }
        binding.btnScanTest.setOnClickListener {
            Intent(requireActivity(), ScanTestActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnCameraTest.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // 创建文件保存路径
            photoFile = createImageFile(requireActivity())
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "${requireActivity().packageName}.fileprovider",
                photoFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            // 启动相机
            cameraPickerLauncher.launch(intent)
        }

        binding.btnMultiScan.setOnClickListener {
            Intent(requireActivity(), CameraXScanActivity::class.java).apply {
                startActivity(this)
            }
        }

        model.text.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        model.textFlow.observe(viewLifecycleOwner) {
            if (TextUtils.isEmpty(it)) {
                return@observe
            }
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private val cameraPickerLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.resultCode == Activity.RESULT_OK) {
                // 照片已保存到指定路径
                val imagePath = photoFile?.absolutePath
                Timber.d("cameraPickerLauncher imagePath=${imagePath}")

            }
        }
    }

    // 获取系统相册图片
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // 例如，你可以将选中的图片显示到 ImageView 中
            binding.imgGallery.setImageURI(uri)
            getMimeType(uri, requireContext()).let {
                //image/jpeg 对应 JPG 文件
                //image/png 对应 PNG 文件
                //image/gif 对应 GIF 文件
                when (it) {
                    "image/jpeg" -> {
                        Timber.d("pickImageLauncher image/jpeg")
                    }
                    "image/png" -> {
                        Timber.d("pickImageLauncher image/png")
                    }
                    "image/gif" -> {
                        Timber.d("pickImageLauncher image/gif")
                    }
                }
            }
//            saveUriToFile(requireContext(), uri, File((requireContext().getExternalFilesDir(null)?.absolutePath
//                ?: "") +"/test.jpg"))
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun getMimeType(uri: Uri, context: Context): String? {
        val contentResolver: ContentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            // 获取 ContentResolver
            val contentResolver: ContentResolver = context.contentResolver
            // 打开输入流并转换为 Bitmap
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    private fun saveUriToFile(context: Context, uri: Uri, destinationFile: File): Boolean {
        try {
            // 获取 ContentResolver
            val contentResolver: ContentResolver = context.contentResolver

            // 打开输入流
            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return false

            // 创建输出流，准备将数据保存到文件
            val outputStream = FileOutputStream(destinationFile)

            // 读取数据流并写入文件
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            // 关闭流
            inputStream.close()
            outputStream.close()

            return true // 成功保存文件
        } catch (e: IOException) {
            e.printStackTrace()
            return false // 失败
        }
    }


}