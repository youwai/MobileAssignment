package com.example.mobileassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileassignment.databinding.FragmentRackListBinding
import androidx.appcompat.app.AppCompatActivity





class RackList : Fragment() {

    private lateinit var binding :FragmentRackListBinding
    private lateinit var manager :RecyclerView.LayoutManager
    private lateinit var viewModelData :ViewModelData
    private var racksAdapter :RecyclerViewAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_rack_list,container,false)
        manager = LinearLayoutManager(requireContext())
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        racksAdapter = RecyclerViewAdapter(viewModelData.rack)
        binding.rackListRecycleView.layoutManager = manager
        binding.rackListRecycleView.adapter = racksAdapter


        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()

                racksAdapter!!.filter.filter(query)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                //adapter.filter.filter(query)

                //binding.rackListRecycleView.adapter

                racksAdapter!!.filter.filter(newText)
                return true
            }

        })



        return binding.root
    }


}