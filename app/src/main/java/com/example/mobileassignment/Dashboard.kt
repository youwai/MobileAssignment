package com.example.mobileassignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileassignment.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Dashboard : Fragment() {

    private lateinit var binding : FragmentDashboardBinding
    private lateinit var manager : RecyclerView.LayoutManager
    private var rack : MutableList<Rack> = mutableListOf()
    private lateinit var viewModelData: ViewModelData
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard,container,false)

        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        manager = LinearLayoutManager(requireContext())


        readMaterial(object : FirestoreCallback {
            override fun onCallback(rack : MutableList<Rack>) {
                viewModelData.rack = rack
                recyclerView()
            }
        })


        return binding.root
    }

    private interface FirestoreCallback {
        fun onCallback(rack: MutableList<Rack>)
    }

    private fun readMaterial(firebaseCallback : FirestoreCallback){

        db.collection("Rack").get().addOnSuccessListener { result ->
            for (document in result) {
                rack.add(Rack(document.data["name"].toString(),document.data["quota"].toString(),document.data["description"].toString()))
            }
            //Log.w("failedAttempt", "yyyyyyyyyyyyyyyyyyyy")
            firebaseCallback.onCallback(rack)
        }
            .addOnFailureListener { exception ->

                Log.w("failedAttempt", "Error getting documents.", exception)
        }


    }

    private fun recyclerView(){
        binding.materialrecycleView.layoutManager = manager
        binding.materialrecycleView.adapter = RecyclerViewAdapter(rack)

    }

}