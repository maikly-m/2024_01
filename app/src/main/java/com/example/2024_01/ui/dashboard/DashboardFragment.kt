package com.example.`2024_01`.ui.dashboard

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.`2024_01`.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView(dashboardViewModel)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView(dashboardViewModel: DashboardViewModel) {
        binding.btnToast.setOnClickListener {
            lifecycle.coroutineScope.launch(Dispatchers.IO) {
                dashboardViewModel.setText("test")
            }
        }
        binding.btnRequest.setOnClickListener {
            dashboardViewModel.getTextFlow()
        }
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        dashboardViewModel.textFlow.observe(viewLifecycleOwner) {
            if (TextUtils.isEmpty(it)) {
                return@observe
            }
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }


}