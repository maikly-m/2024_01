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
import androidx.navigation.fragment.findNavController
import com.example.u.R
import com.example.u.databinding.FragmentHomeBinding
import com.permissionx.guolindev.PermissionX
import org.jsoup.Jsoup

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

        binding.btnPermission.setOnClickListener {
            findNavController().navigate(R.id.home2permission)
        }
        binding.btnRichText.setOnClickListener {
            findNavController().navigate(R.id.home2rich_text)
        }
        binding.btnTextTest.setOnClickListener {
            findNavController().navigate(R.id.home2text_test)
        }
        binding.btnCamera.setOnClickListener {
            findNavController().navigate(R.id.home2camera)
        }
        binding.btnRecycleView.setOnClickListener {
            findNavController().navigate(R.id.home2recycle_view)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

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