package com.example.u.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.u.databinding.FragmentLoadMoreBinding
import com.example.u.databinding.ItemCardViewBinding
import com.example.u.databinding.ItemNoMoreViewBinding

class LoadMoreFragment : Fragment() {

    private lateinit var tvCountDel: TextView
    private lateinit var btnDel: Button
    private var showManager: Boolean = false
    private var _binding: FragmentLoadMoreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var removeIndex: MutableSet<Int>
    private lateinit var viewModel: LoadMoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(LoadMoreViewModel::class.java)

        _binding = FragmentLoadMoreBinding.inflate(inflater, container, false)
        val root: View = binding.root
        swipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView = binding.recyclerView
        btnDel = binding.btnDel
        tvCountDel = binding.tvCountDel

        removeIndex = viewModel.removeIndex

        // 初始化 RecyclerView 和 Adapter
        adapter = CardAdapter(viewModel.allData)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener {
            // 下拉刷新时清空数据并重新加载第一页
            viewModel.refreshData()
            removeIndex.clear()
            tvCountDel.text = "已选择"+removeIndex.size
        }

        // 上拉加载更多的监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (totalItemCount <= (lastVisibleItem + 2)) {
                    viewModel.loadMoreData()
                }
            }
        })

        viewModel.isRefreshing.observe(viewLifecycleOwner){
            swipeRefreshLayout.isRefreshing = it
        }
        viewModel.refreshRecyclerView.observe(viewLifecycleOwner){
            if (it) {
                if (adapter.data.last() is Item.NoMoreData &&
                    adapter.data.size < 7){
                    // 移除
                    adapter.data.removeAt(adapter.data.size - 1)
                }
                adapter.notifyDataSetChanged()
            } else {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firsVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (firsVisibleItem > 0) {
                    // 第一项看不见的时候
                    // 没有更多数据
                    if (adapter.data.last() !is Item.NoMoreData){
                        adapter.data.add(Item.NoMoreData)
                        adapter.notifyItemInserted(  adapter.data.size - 1)
                    }
                }
            }

        }

        binding.btnCount.setOnClickListener {
            if (binding.llCountDel.visibility == View.GONE) {
                binding.llCountDel.visibility = View.VISIBLE
                showManager = true
            } else {
                binding.llCountDel.visibility = View.GONE
                showManager = false
            }
            removeIndex.clear()

            tvCountDel.text = "已选择"+removeIndex.size
            adapter.notifyDataSetChanged()
        }


        binding.btnDel.setOnClickListener {
            binding.llCountDel.visibility = View.GONE
            showManager = false

            adapter.removeItems(removeIndex)
        }

        if (viewModel.allData.isEmpty()){
            // 初始加载数据
            viewModel.loadMoreData()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TYPE_DATA = 1
        private const val TYPE_NO_MORE_DATA = 2
    }

    inner class CardAdapter(val data: MutableList<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class MyCardViewHolder(private val binding: ItemCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                // 删除点击事件
                itemView.setOnLongClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        // 这里可以删除对应的数据
                        data.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }
                    true
                }

                binding.rb.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        if (isChecked){
                            removeIndex.add(adapterPosition)
                        }else{
                            removeIndex.remove(adapterPosition)
                        }
                        recyclerView.post {
                            tvCountDel.text = "已选择"+removeIndex.size
                        }
                    }
                }

            }

            fun bind(data: Item.DataItem) {
                binding.item = data.name
                binding.executePendingBindings()

                binding.textView.text = data.name
                if (showManager){
                    binding.rb.visibility = View.VISIBLE
                }else{
                    binding.rb.visibility = View.GONE
                }
                if (adapterPosition in removeIndex) {
                    binding.rb.isChecked = true
                } else {
                    binding.rb.isChecked = false
                }
                // Timber.d("bind adapterPosition=${adapterPosition}, layoutPosition=${layoutPosition}")
            }
        }
        inner class NoMoreDataViewHolder(private val binding: ItemNoMoreViewBinding) : RecyclerView.ViewHolder(binding.root) {
            init {

            }

            fun bind(s: String) {
                binding.item = s
                binding.textView.text = s
                binding.executePendingBindings()
                // Timber.d("bind adapterPosition=${adapterPosition}, layoutPosition=${layoutPosition}")
            }
        }
        // 创建新的卡片视图
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_DATA -> {
                    val view = ItemCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    MyCardViewHolder(view)
                }
                TYPE_NO_MORE_DATA -> {
                    val view = ItemNoMoreViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    NoMoreDataViewHolder(view)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        // 绑定数据到卡片视图
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is MyCardViewHolder -> {
                    holder.bind(data[position] as Item.DataItem)
                }
                is NoMoreDataViewHolder -> {
                    // 你可以在此处设置提示文字
                    holder.bind("没有更多数据了")
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (data[position]) {
                is Item.DataItem -> TYPE_DATA
                is Item.NoMoreData -> TYPE_NO_MORE_DATA
            }
        }

        // 返回列表的大小
        override fun getItemCount(): Int {
            return data.size
        }

        fun removeItems(removes: MutableSet<Int>) {
            // 从大到小的顺序移除元素
            removes.sortedDescending().forEach { index ->
                if (index in data.indices) {  // 确保索引有效
                    data.removeAt(index)
                }
            }
            viewModel.refreshRecyclerView(true)
        }
    }


}
// Adapter中的数据模型类
sealed class Item {
    data class DataItem(val id: String, val name: String) : Item()
    data object NoMoreData : Item()  // 无数据项
}
