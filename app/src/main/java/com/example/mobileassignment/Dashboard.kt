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
import java.text.SimpleDateFormat
import java.util.*


class Dashboard : Fragment() {

    private lateinit var binding : FragmentDashboardBinding
    private lateinit var manager : RecyclerView.LayoutManager
    private var rackData : MutableList<Rack> = mutableListOf()
    private lateinit var viewModelData: ViewModelData
    private val db = Firebase.firestore
    private var someHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard,container,false)

        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        manager = LinearLayoutManager(requireContext())


        readMaterial(object : FirestoreCallback {
            override fun onCallback() {

                getUsedQuota(object : GetUsedQuota {
                    override fun onCallback() {

                        Log.v("Run 1 Only","Cincai")
                        viewModelData.rack = rackData

                        recyclerView()
                    }
                })

            }
        })

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

    private interface GetUsedQuota{
        fun onCallback()
    }

    private fun readMaterial(firebaseCallback : FirestoreCallback){

        db.collection("Rack").get().addOnSuccessListener { result ->
            for (document in result) {
                rackData.add(Rack(document.data["name"].toString(),document.data["quota"].toString(),document.data["description"].toString(),"0"))
                Log.v("Read From DB",document.data["name"].toString())
            }

            firebaseCallback.onCallback()
        }
            .addOnFailureListener { exception ->

                Log.w("failedAttempt", "Error getting documents.", exception)
        }

    }

    private fun getUsedQuota(usedQuota : GetUsedQuota){

        val size = rackData.size - 1

        Log.v("Rack Size",rackData[size].rackName)

        for(rack in rackData){

            db.collection("Rack").document(rack.rackName).collection("Materials").get().addOnSuccessListener {

                if(rack.rackName != rackData[size].rackName) {
                    rack.usedQuota=  it.size().toString()
                    Log.v("Rack Name:",rack.rackName)
                    Log.v("Test Except Last",rack.usedQuota)
                }else {
                    rack.usedQuota = it.size().toString()
                    Log.v("Rack Name:",rack.rackName)
                    Log.v("This is Last",rack.usedQuota)
                    usedQuota.onCallback()
                }
                //Log.v("Test in Dashboard",rack.usedQuota)

            }.addOnFailureListener { exception ->

                Log.w("failedAttempt", "Error getting documents.", exception)
            }
        }
    }

    private fun recyclerView(){
        binding.materialrecycleView.layoutManager = manager
        binding.materialrecycleView.adapter = RecyclerViewAdapter(filterLowQuotaRack())

    }

    private fun filterLowQuotaRack():MutableList<Rack>{

        val lowQuotaRack :MutableList<Rack> = mutableListOf()

        for(rack in viewModelData.rack){

            if((rack.quota.toInt() - rack.usedQuota.toInt()) < 10) {
                lowQuotaRack.add(rack)
            }
        }

        return lowQuotaRack

    }

    override fun onDestroy() {
        super.onDestroy()
        someHandler?.removeCallbacksAndMessages(null)
        someHandler = null
    }



}