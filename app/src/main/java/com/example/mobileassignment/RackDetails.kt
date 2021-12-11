package com.example.mobileassignment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.mobileassignment.databinding.FragmentRackDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import android.widget.TextView


class RackDetails (private val selectedRack: Rack) : Fragment() {
    private lateinit var binding: FragmentRackDetailsBinding
    private val db = Firebase.firestore

    private val materialsOnlist: ArrayList<Materials> = ArrayList()
    private val materialsOutlist: ArrayList<Materials> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rack_details, container, false)

        binding.rack = selectedRack



        readData(object : FirestoreCallback {
            override fun onCallback(materialsList: ArrayList<Materials>) {
                Log.d("check", "completed")

                if(materialsOnlist.size > 0) {
                    binding.materialOnlist.adapter =
                        MaterialListAdapter(requireActivity(), materialsOnlist)
                }
                if(materialsOutlist.size > 0) {
                    binding.materialOutlist.adapter =
                        MaterialListAdapter(requireActivity(), materialsOutlist)
                }
            }
        })

        // List for still on rack materials
        binding.materialOnlist.isClickable = true
        binding.materialOnlist.emptyView = binding.emptyOnlist
        binding.materialOnlist.setOnItemClickListener { parent, view, position, id ->
            val materialClicked = materialsOnlist[position]

            var dialog = DialogBoxFragment(materialClicked)

            dialog.show(requireActivity().supportFragmentManager, "customDialog")
        }

        // List for retrieved materials
        binding.materialOutlist.isClickable = true
        binding.materialOutlist.emptyView = binding.emptyOutlist
        binding.materialOutlist.setOnItemClickListener { parent, view, position, id ->
            val materialClicked = materialsOutlist[position]

            var dialog = DialogBoxFragment(materialClicked)

            dialog.show(requireActivity().supportFragmentManager, "customDialog")
        }

        return binding.root
    }

    private fun readData(firestoreCallback: FirestoreCallback) {
        db.collection("Rack").document(selectedRack.rackName).collection("Materials")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val material = document.toObject<Materials>()

                    when (material.status) {
                        2 -> materialsOnlist.add(material)
                        3 -> materialsOutlist.add(material)
                    }
                }
                firestoreCallback.onCallback(materialsOnlist)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private interface FirestoreCallback {
        fun onCallback(materialsList: ArrayList<Materials>)
    }

}