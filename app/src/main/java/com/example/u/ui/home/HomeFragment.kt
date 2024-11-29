package com.example.u.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

        val editText: EditText = binding.textHome
        // 替换闪烁的cursor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above (API 29+)
            val cursorDrawable = resources.getDrawable(R.drawable.custom_cursor, null)
            editText.textCursorDrawable = cursorDrawable
        } else {
            // For Android 9 and below
            val editorField = EditText::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(editText)

            val cursorDrawable = resources.getDrawable(R.drawable.custom_cursor)
            val cursorField = editor.javaClass.getDeclaredField("mCursorDrawable")
            cursorField.isAccessible = true
            cursorField.set(editor, arrayOf(cursorDrawable, cursorDrawable))
        }

        //editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        homeViewModel.text.observe(viewLifecycleOwner) {

        }

        // spannableString
        val tvTip: TextView = binding.tvTip
        val spannableString = SpannableString("123abc345fgh6666ffflipokkkkk")
        spannableString.setSpan(ForegroundColorSpan(Color.GRAY), 3,
            6, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), 8,
            11, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
//        // 为“bold”部分应用粗体样式
//        spannableString.setSpan(
//            StyleSpan(Typeface.BOLD),  // 设置粗体样式
//            10, 14,  // 开始和结束索引
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        // 为“italic”部分应用斜体样式
//        spannableString.setSpan(
//            StyleSpan(Typeface.ITALIC),  // 设置斜体样式
//            15, 21,  // 开始和结束索引
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        // 为“underline”部分添加下划线
//        spannableString.setSpan(
//            UnderlineSpan(), // 添加下划线
//            10, 18, // 开始和结束索引
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
        // 为“Click here”部分添加点击事件
        spannableString.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                // super.updateDrawState(ds)
            }

            override fun onClick(widget: View) {
                // 在点击后修改背景颜色
                // 改变背景颜色
                spannableString.setSpan(
                    BackgroundColorSpan(Color.TRANSPARENT), // 改为原来透明颜色
                    3, 6, // "Click here" 部分
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // 更新 TextView 内容以刷新背景颜色
                widget.invalidate()
                // 处理点击事件
                Toast.makeText(requireContext(), "Clicked on Click here", Toast.LENGTH_SHORT).show()
            }
        }, 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTip.setText(spannableString)
        tvTip.movementMethod = LinkMovementMethod.getInstance()

        binding.btnPermission.setOnClickListener {
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