package com.example.u.ui.test

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.u.R
import com.example.u.databinding.FragmentCustomBinding
import com.example.u.ui.view.WrapContentLayoutManager

class CustomFragment : Fragment() {

    private var _binding: FragmentCustomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCustomBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Glide.with(requireContext())
            .load(requireContext().getDrawable(R.drawable.bg_test_code))  // 指定图片 URL 或资源 ID
            .circleCrop()
            .into( binding.ivGlide)

        binding.btnDownloadAndInstall.setOnClickListener {
            findNavController().navigate(R.id.custom_view2download_and_install)
        }

        val recyclerView: RecyclerView = binding.rv

        // 设置自定义的 LayoutManager
        val layoutManager = WrapContentLayoutManager()
        recyclerView.layoutManager = layoutManager

        // 设置 Adapter
        val items = arrayListOf<String>("test",
            "test12",
            "test312",
            "test1ddd2rr",
            "test12",
//            "testtt1fff2",
//            "testttt12",
//            "testttt12",
//            "testtsdasdsatt12",
//            "testtsdasdsatt12",
//            "testdsadsad12",
//            "testttt12",
//            "testttt12",
//            "testttsdadsaddddt12",
//            "testttsdadsadt12",
//            "testttsdadsadt12dddddd",
//            "testttsdadsadt12",
//            "testttsdadsadt12",
//            "testt12",
//            "testttdsadt12",
//            "testttt12",
//            "tesdsatt12",
//            "tesdsatt12",
//            "tesdsatt12",
//            "tesssssdsatt12",
//            "tesdsatt12",
//            "tesdsatsssst12",
            "testttt12",
            "testttdsadadadt12",
            "testttsdsddddddsadasdt12",
            "testdsdttt12",
            "testt12",
            "test23")
        recyclerView.adapter = CustomAdapter(items)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener{
            val params = recyclerView.layoutParams
            params.height = layoutManager.getTotalHeight()
            recyclerView.layoutParams = params
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class CustomAdapter(private val items: List<String>) :
        RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.test_text, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.textView.text = items[position]
            holder.textView.setTextColor(Color.BLACK)
            if (position%2==0) {
                holder.textView.setBackgroundColor(Color.YELLOW)
            } else {
                holder.textView.setBackgroundColor(Color.GRAY)
            }
        }

        override fun getItemCount(): Int = items.size

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.header_title)
            val ll: LinearLayout = view.findViewById(R.id.ll)
        }
    }

}