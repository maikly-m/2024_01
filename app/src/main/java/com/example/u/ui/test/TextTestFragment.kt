package com.example.u.ui.test

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.R
import com.example.u.databinding.FragmentNotificationsBinding
import com.example.u.databinding.FragmentTextTestBinding
import com.example.u.ui.notifications.NotificationsViewModel

class TextTestFragment : Fragment() {

    private var _binding: FragmentTextTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(TextTestViewModel::class.java)

        _binding = FragmentTextTestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val editText: EditText = binding.et01
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

        // spannableString
        val tvTip: TextView = binding.tvTip
        val spannableString = SpannableString("123abc345fgh6666ffflipokkkkk")
        spannableString.setSpan(
            ForegroundColorSpan(Color.GRAY), 3,
            6, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), 8,
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}