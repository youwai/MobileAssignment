package com.example.mobileassignment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class RackList : Fragment() {

    private lateinit var binding: FragmentRackListBinding
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var viewModelData: ViewModelData
    private var racksAdapter: RecyclerViewAdapter? = null
    private var rackData: MutableList<Rack> = mutableListOf()
    private val db = Firebase.firestore

    @SuppressLint("SimpleDateFormat")
    private val todayDate: String = SimpleDateFormat("dd/MM/yyyy").format(Date()).toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rack_list, container, false)
        manager = LinearLayoutManager(requireContext())
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        return binding.root
    }

    //Reason of put the code inside the resume: To prevent the data stack when resume the fragment
    override fun onResume() {
        super.onResume()

        //Clear the data inside the rackData before read the data from database.
        rackData.clear()
        viewModelData.resetDbData()

        readMaterial(object : FirestoreCallback {
            override fun onCallback() {

                //This function is to sort the data read from the database.
                rackData.sortWith(compareByDescending { it.rackNo })
                rackData = rackData.asReversed()

                viewModelData.racks.addAll(rackData)
                //To Start the Recycle View
                recyclerView()
            }
        })

    }

    private fun readMaterial(firebaseCallback: FirestoreCallback) {

        db.collection("Rack").get().addOnSuccessListener { result ->
            for (document in result) {
                db.collection("Rack").document(document.data["name"].toString())
                    .collection("Materials").get().addOnSuccessListener {
                        var usedQuota: Int = 0

                        for (materials in it) {
                            val material = materials.toObject<Materials>()

                            if (material.status == 2) {
                                usedQuota += 1
                            }

                            when (material.status) {
                                2 -> {
                                    if (material.rackInDate.equals(todayDate))
                                        viewModelData.todayInMaterial.add(material)
                                }
                                3 -> {
                                    if (material.rackOutDate.equals(todayDate))
                                        viewModelData.todayOutMaterial.add(material)
                                }
                            }
                        }
                        rackData.add(
                            Rack(
                                document.data["name"].toString(),
                                document.data["quota"].toString(),
                                document.data["description"].toString(),
                                usedQuota.toString(),
                                document.data["no"].toString().toInt()
                            )
                        )
                        if (rackData.size.equals(result.size()))
                            firebaseCallback.onCallback()

                    }.addOnFailureListener { exception ->
                        Log.w("failedAttempt", "Error getting documents.", exception)
                    }
            }
        }.addOnFailureListener { exception ->

            Log.w("failedAttempt", "Error getting documents.", exception)
        }

    }

    private fun recyclerView() {
        binding.rackListRecycleView.layoutManager = manager
        racksAdapter = RecyclerViewAdapter(rackData, viewModelData)
        binding.rackListRecycleView.adapter = racksAdapter

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()

                racksAdapter!!.filter.filter(query)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                racksAdapter!!.filter.filter(newText)
                return true
            }

        })

    }

    private interface FirestoreCallback {
        fun onCallback()
    }



}