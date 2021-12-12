package com.example.mobileassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentTodayInListBinding


class todayInList : Fragment() {

    private lateinit var binding: FragmentTodayInListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)
        val records = viewModelData.todayInMaterial

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today_in_list, container,false)

        binding.todayInList.adapter = todayListAdapter(requireActivity(), records, "in")

        // make the list item clickable
        binding.todayInList.isClickable = true
        binding.todayInList.emptyView = binding.emptyInList
        binding.todayInList.setOnItemClickListener { parent, view, position, id ->
            val materialClicked = records[position]

            var dialog = DialogBoxFragment(materialClicked)

            dialog.show(requireActivity().supportFragmentManager, "materialDialog")
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}