package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.databinding.FragmentPastProfileListBinding

class PastProfileListFragment : Fragment() {

    private var _binding: FragmentPastProfileListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPastProfileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemListAdapter = ItemListAdapter({
            val action = ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment2() // ToDo navgraphで引数を渡す
            this.findNavController().navigate(action)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}