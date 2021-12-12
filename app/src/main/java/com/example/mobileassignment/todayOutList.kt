package com.example.mobileassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentTodayOutListBinding


class todayOutList : Fragment() {

    private lateinit var binding: FragmentTodayOutListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)
        val records = viewModelData.todayOutMaterial

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today_out_list, container,false)

        binding.todayOutList.adapter = todayListAdapter(requireActivity(), records, "out")

        // make the list item clickable
        binding.todayOutList.isClickable = true
        binding.todayOutList.emptyView = binding.emptyOutList
        binding.todayOutList.setOnItemClickListener { parent, view, position, id ->
            val materialClicked = records[position]

            var dialog = DialogBoxFragment(materialClicked)

            dialog.show(requireActivity().supportFragmentManager, "materialDialog")
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}