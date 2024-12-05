package com.example.u.ui.test

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u.provider.GetProvider
import com.example.u.uitls.thread.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class LoadMoreViewModel : ViewModel() {

    private var page = 1
    private val pageSize = 4
    val allData = mutableListOf<Item>()  // 存储所有数据，防止重复加载
    val removeIndex = mutableSetOf<Int>()  // 移除项

    private val _isRefreshing = SingleLiveEvent<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _refreshRecyclerView = SingleLiveEvent<Boolean>()
    val refreshRecyclerView: LiveData<Boolean> = _refreshRecyclerView

    fun refreshData() {
        viewModelScope.launch {
            page = 1
            allData.clear()  // 清空数据
            loadMoreData()   // 重新加载第一页
        }
    }

    fun refreshRecyclerView(boolean: Boolean): Unit {
        _refreshRecyclerView.postValue(boolean)
    }

    fun loadMoreData() {
        viewModelScope.launch {
            _isRefreshing.postValue(page == 1) // 下拉刷新时显示刷新动画

            // 模拟网络请求加载数据
            val newItems = fetchDataFromServer(page, pageSize)
            Timber.d("loadMoreData newItems.size=${newItems.size}")
            if (newItems.isEmpty()) {
                _isRefreshing.postValue(false) // 停止刷新动画
                _refreshRecyclerView.postValue(false)
                return@launch
            }

            if (page == 1) {
                allData.clear()  // 清空之前的数据
            }
            // 去重：只添加那些没有出现过的 Item
            val uniqueItems = newItems.filterNot { newItem ->
                allData.map {
                    it as Item.DataItem
                }.any { it.id == newItem.id }
            }
            Timber.d("loadMoreData uniqueItems.size=${uniqueItems.size}")
            if (uniqueItems.isNotEmpty()) {
                allData.addAll(uniqueItems)
                _refreshRecyclerView.postValue(true)
            }

            page++ // 下一页
            _isRefreshing.postValue(false) // 停止刷新动画

            Timber.d("loadMoreData allData.size=${allData.size}")
        }
    }

    // 模拟从服务器获取数据
    private fun fetchDataFromServer(page: Int, pageSize: Int): List<Item.DataItem> {
        if (page > 4) {
            return arrayListOf()
        }
        // 这里是模拟数据，实际上应该是通过 Retrofit 或其他网络库加载
        return List(pageSize) {
            Item.DataItem("id_${(page - 1) * pageSize + it}", "Item ${(page - 1) * pageSize + it}")
        }
    }
}