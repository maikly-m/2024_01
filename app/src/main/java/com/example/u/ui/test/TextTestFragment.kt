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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u.R
import com.example.u.databinding.FragmentTextTestBinding
import com.example.u.dialog.ConfirmDialog
import com.github.gzuliyujiang.dialog.DialogConfig
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.github.gzuliyujiang.wheelpicker.SexPicker
import com.github.gzuliyujiang.wheelview.annotation.CurtainCorner
import com.github.gzuliyujiang.wheelview.contract.TextProvider
import java.io.Serializable


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

//        MaterialDialog(requireContext())
//            .show {
//
//            title(text = "test")
//            listItems(items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")) { _, index, _ ->
//                Toast.makeText(requireContext(), "Selected: ${index + 1}", Toast.LENGTH_SHORT).show()
//            }
//            negativeButton(android.R.string.cancel)
//        }

        binding.btnTestPopupWindow.setOnClickListener {
            //te()
            onOptionBean()
        }


        binding.btnTestDialog.setOnClickListener {
            val c = ConfirmDialog.newInstance(true)
                .setTitle("测试")
                .setMessage("test  more  data ")
                .setPositiveButton("确定", null)
                .setCancelableOut(false)
                .setNegativeButton("取消", object : ConfirmDialog.OnDialogClickListener{
                    override fun onClick(dialog: DialogFragment) {
                        dialog.dismissAllowingStateLoss()
                    }
                })

                c.show(this)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onOptionBean() {
        val data: MutableList<GoodsCategoryBean?> = ArrayList<GoodsCategoryBean?>()
        data.add(GoodsCategoryBean(1, "食品生鲜"))
        data.add(GoodsCategoryBean(2, "家用电器"))
        data.add(GoodsCategoryBean(3, "家居生活"))
        data.add(GoodsCategoryBean(4, "医疗保健"))
        data.add(GoodsCategoryBean(5, "酒水饮料"))
        data.add(GoodsCategoryBean(6, "图书音像"))

        DialogConfig.getDialogColor().cancelTextColor(Color.BLUE)
        DialogConfig.getDialogColor().okTextColor(Color.BLUE)

        val picker = OptionPicker(requireActivity())
        picker.okView.textSize = 20f
        picker.topLineView.visibility = View.GONE

        picker.setTitle("货物分类")
        picker.setBodyWidth(140)
        picker.setData(data)
        picker.setDefaultPosition(2)
        picker.setOnOptionPickedListener { position, item ->
            Toast.makeText(
                requireContext(),
                "$position-$item",
                Toast.LENGTH_SHORT
            ).show();
        }
        val wheelLayout = picker.wheelLayout
        wheelLayout.setIndicatorEnabled(false)
        wheelLayout.setTextColor(Color.GRAY)
        wheelLayout.setSelectedTextColor(Color.BLUE)
        wheelLayout.setTextSize(15 * requireContext().resources.displayMetrics.scaledDensity)
        //注：建议通过`setStyle`定制样式设置文字加大，若通过`setSelectedTextSize`设置，该解决方案会导致选择器展示时跳动一下
        //wheelLayout.setStyle(R.style.WheelStyleDemo);
        wheelLayout.setSelectedTextSize(17 * requireContext().resources.displayMetrics.scaledDensity)
        wheelLayout.setSelectedTextBold(true)
        wheelLayout.setCurtainEnabled(true)
        wheelLayout.setCurtainColor(0x11010000)
        wheelLayout.setCurtainCorner(CurtainCorner.ALL)
        wheelLayout.setCurtainRadius(5 * requireContext().resources.displayMetrics.density)
        wheelLayout.setOnOptionSelectedListener { position, item ->
            picker.titleView.text = picker.wheelView.formatItem(position)
        }

        picker.show()
    }

    fun te(): Unit {
        val picker = SexPicker(requireActivity())
        picker.setBodyWidth(140)
        picker.setIncludeSecrecy(false)
        picker.setDefaultValue("女")
        picker.setOnOptionPickedListener { position, item ->
            Toast.makeText(
                requireContext(),
                "$position-$item",
                Toast.LENGTH_SHORT
            ).show();
        }
        picker.wheelLayout.setOnOptionSelectedListener { position, item ->
            picker.titleView.text = picker.wheelView.formatItem(position)
        }
        picker.show()
    }

}
class GoodsCategoryBean(var id: Int, var name: String) : Serializable, TextProvider {
    override fun provideText(): String {
        return name
    }
    override fun toString(): String {
        return "GoodsCategoryBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }
}