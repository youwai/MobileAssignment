package com.example.mobileassignment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class Dashboard : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var viewModelData: ViewModelData
    private var rackData: MutableList<Rack> = mutableListOf()
    private val db = Firebase.firestore
    private var someHandler: Handler? = null

    @SuppressLint("SimpleDateFormat")
    private val todayDate: String = SimpleDateFormat("dd/MM/yyyy").format(Date()).toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)
        manager = LinearLayoutManager(requireContext())

        // set up date in dashbaord
        someHandler = Handler(getMainLooper())
        someHandler!!.postDelayed(object : Runnable {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                binding.date.text = SimpleDateFormat("(EEE) dd MMM, yyyy      hh:mm:ss a").format(
                    Date()
                )
                someHandler!!.postDelayed(this, 1000)
            }
        }, 10)

        binding.todayInCardView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, todayInList())
                .addToBackStack(null)
                .commit()
        }

        binding.todayOutCardView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, todayOutList())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //Clear the data before read data from the data base to prevent data stack.
        rackData.clear()
        viewModelData.resetDbData()

        readMaterial(object : FirestoreCallback {
            override fun onCallback() {

                //This function is to Rearrange the data read from the database and store in to viewModel Data
                rackData.sortWith(compareByDescending { it.rackName })
                rackData = rackData.asReversed()

                //Set Total Racks to View
                binding.totalRack.text = rackData.size.toString()

                //To Filter low quota Rack
                filterLowQuotaRack()

                //Set Data to the view
                binding.lowQuotaRack.text = rackData.size.toString()
                binding.todayIn.text = viewModelData.todayInMaterial.size.toString()
                binding.todayOut.text = viewModelData.todayOutMaterial.size.toString()

                //Start the Recycle View
                recyclerView()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        someHandler?.removeCallbacksAndMessages(null)
        someHandler = null
    }

    private fun readMaterial(firebaseCallback: FirestoreCallback) {

        //Read the data from database.
        db.collection("Rack").get().addOnSuccessListener { result ->
            for (document in result) {
                db.collection("Rack").document(document.data["name"].toString())
                    .collection("Materials").get().addOnSuccessListener {
                        rackData.add(
                            Rack(
                                document.data["name"].toString(),
                                document.data["quota"].toString(),
                                document.data["description"].toString(),
                                it.size().toString()
                            )
                        )
                        for (materials in it) {
                            val material = materials.toObject<Materials>()
                            if (material.rackInDate.equals(todayDate)) {
                                when (material.status) {
                                    2 -> viewModelData.todayInMaterial.add(material)
                                    3 -> viewModelData.todayOutMaterial.add(material)
                                }
                            }
                        }
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

    private fun filterLowQuotaRack() {
        val lowQuotaRack: MutableList<Rack> = mutableListOf()

        for (rack in rackData) {

            if ((rack.quota.toInt() - rack.usedQuota.toInt()) < 10) {

                lowQuotaRack.add(rack)
            }
        }
        rackData.clear()
        rackData.addAll(lowQuotaRack)
    }

    private fun recyclerView() {
        binding.materialrecycleView.layoutManager = manager
        binding.materialrecycleView.adapter = RecyclerViewAdapter(rackData, viewModelData)

    }


    private interface FirestoreCallback {
        fun onCallback()
    }

}