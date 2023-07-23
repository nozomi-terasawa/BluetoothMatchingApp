package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.adapter.AdvertiseAdapter
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentAdvertiseListBinding

class AdvertiseListFragment : Fragment() {
    private var _binding: FragmentAdvertiseListBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvertiseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val advertiseAdapter = AdvertiseAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = advertiseAdapter

        fireStore.getAdvertise(advertiseAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}