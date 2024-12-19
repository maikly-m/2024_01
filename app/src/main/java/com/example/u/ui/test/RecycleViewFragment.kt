package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.u.databinding.FragmentRecycleViewBinding
import com.example.u.databinding.ItemCardViewBinding
import com.example.u.databinding.RecycleViewItemBinding
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

        val recyclerView3 = binding.recyclerView3
        // 设置 GridLayoutManager，列数为 3
        recyclerView3.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        // 创建适配器并设置给 RecyclerView
        val items3 = List(30) { "Card ${it + 1}" }  // 创建 30 个卡片
        recyclerView3.adapter = CardAdapter(items3)
        recyclerView3.isNestedScrollingEnabled = false

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
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
                if (binding.tvMore.visibility == View.GONE){
                    binding.tvMore.visibility = View.VISIBLE
                }else{
                    binding.tvMore.visibility = View.GONE
                }
            }
        }

        fun bind(item: String) {
            binding.item = item
            // 强制立即更新绑定数据到视图
            binding.executePendingBindings()
        }
    }

    inner class CardAdapter(private val items: List<String>) : RecyclerView.Adapter<MyCardViewHolder>() {
        // 创建新的卡片视图
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCardViewHolder {
            val view = ItemCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyCardViewHolder(view)
        }

        // 绑定数据到卡片视图
        override fun onBindViewHolder(holder: MyCardViewHolder, position: Int) {
            holder.bind(items[position])
        }

        // 返回列表的大小
        override fun getItemCount(): Int {
            return items.size
        }
    }


    // ViewHolder class to hold item views
    inner class MyCardViewHolder(private val binding: ItemCardViewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(item: String) {
            binding.item = item
            binding.executePendingBindings()

            binding.textView.text = item
        }
    }
}

