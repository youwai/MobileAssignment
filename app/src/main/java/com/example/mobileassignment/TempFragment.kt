package com.example.mobileassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentTempBinding


class TempFragment : Fragment() {

    private lateinit var binding :FragmentTempBinding
    private lateinit var viewModelData :ViewModelData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_temp,container,false)
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        binding.viewmodelData = viewModelData





        return binding.root
    }

}