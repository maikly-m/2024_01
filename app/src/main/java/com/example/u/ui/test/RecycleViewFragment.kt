package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.u.databinding.FragmentRecycleViewBinding
import com.example.u.databinding.RecycleViewItemBinding
import com.example.u.ui.viewolder.ViewHolder
import timber.log.Timber

class RecycleViewFragment : Fragment() {

    private var _binding: FragmentRecycleViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecycleViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val items = List(30) { "Item $it" }
        val recyclerView = binding.recyclerView
        // 设置 RecyclerView 的适配器
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MyAdapter(items)
        // 禁用 RecyclerView 滚动
        recyclerView.isNestedScrollingEnabled = false

        val items2 = List(10) { "Item2 $it" }
        val recyclerView2 = binding.recyclerView2
        // 设置 RecyclerView 的适配器
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        recyclerView2.adapter = MyAdapter(items2)
        // 禁用 RecyclerView 滚动
        recyclerView2.isNestedScrollingEnabled = false

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    inner class MyAdapter(private val items: List<String>) : RecyclerView.Adapter<ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = RecycleViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding as ViewDataBinding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val binding: RecycleViewItemBinding = holder.mBinding as RecycleViewItemBinding
//        binding.tvItem.text = (items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//}
    inner class MyAdapter(private val items: List<String>) : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = RecycleViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }


    // ViewHolder class to hold item views
    inner class MyViewHolder(private val binding: RecycleViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set the click listener for the item view
            binding.cl.setOnClickListener {
                val item = binding.item
                item?.let {
                    Timber.d("MyViewHolder click $item")
                }
            }
        }

        fun bind(item: String) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}

