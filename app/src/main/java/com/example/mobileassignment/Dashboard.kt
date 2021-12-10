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
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*


class Dashboard : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var manager: RecyclerView.LayoutManager
    private var rackData: MutableList<Rack> = mutableListOf()
    private lateinit var viewModelData: ViewModelData
    private val lowQuotaRack: MutableList<Rack> = mutableListOf()
    private val db = Firebase.firestore
    private var someHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)

        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        viewModelData.rack = rackData

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


        return binding.root
    }

    private interface FirestoreCallback {
        fun onCallback()
    }

    private interface GetUsedQuota {
        fun onCallback()
    }

    private fun readMaterial(firebaseCallback: FirestoreCallback) {

        db.collection("Rack").get().addOnSuccessListener { result ->
            for (document in result) {

                db.collection("Rack").document(document.data["name"].toString()).collection("Materials").get().addOnSuccessListener {

                    rackData.add(
                        Rack(
                            document.data["name"].toString(),
                            document.data["quota"].toString(),
                            document.data["description"].toString(),
                            it.size().toString()
                        )
                    )
                    if(rackData.size.equals(result.size()))
                        firebaseCallback.onCallback()
                    Log.v("Rack Dt", rackData.toString())
                    Log.v("Result Size", result.size().toString())
                }.addOnFailureListener { exception ->
                    Log.w("failedAttempt", "Error getting documents.", exception)
                }

                Log.v("Read From DB", document.data["name"].toString())
            }


        }.addOnFailureListener { exception ->

                Log.w("failedAttempt", "Error getting documents.", exception)
            }

    }

    private fun recyclerView() {
        binding.materialrecycleView.layoutManager = manager
        binding.materialrecycleView.adapter = RecyclerViewAdapter(lowQuotaRack)

    }

    private fun filterLowQuotaRack() {

        for (rack in viewModelData.rack) {

            Log.v("Filter Rack Name", rack.rackName)
            Log.v("Test Filter", rack.usedQuota)
            if ((rack.quota.toInt() - rack.usedQuota.toInt()) < 10) {
                Log.v("Add to Low List", rack.rackName)
                lowQuotaRack.add(rack)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        someHandler?.removeCallbacksAndMessages(null)
        someHandler = null
    }

    override fun onPause() {
        super.onPause()

        lowQuotaRack.clear()

    }

    override fun onResume() {
        super.onResume()
        rackData.clear()

        readMaterial(object : FirestoreCallback {
            override fun onCallback() {
                Log.v("Rack Dt 2", rackData.toString())
                filterLowQuotaRack()
                binding.totalRack.text = viewModelData.rack.size.toString()
                binding.lowQuotaRack.text = lowQuotaRack.size.toString()

                recyclerView()
                Log.v("Navigation", "1")
            }
        })

    }

}