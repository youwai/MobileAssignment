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
    private val lowQuotaRack :MutableList<Rack> = mutableListOf()
    private val db = Firebase.firestore
    private var someHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dashboard,container,false)

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

        val last = rackData.last()

        Log.v("Rack Data",rackData.toString())


        rackData.forEach { rack ->

            Log.v("Rack In",rack.toString())
            db.collection("Rack").document(rack.rackName).collection("Materials").get().addOnSuccessListener {

                /*
                if(rack.rackName != rackData[size].rackName) {
                    rack.usedQuota=  it.size().toString()
                    Log.v("No Last Rack Name:",rack.rackName)
                    Log.v("Used Quota",rack.usedQuota)
                }else {
                    rack.usedQuota = it.size().toString()
                    Log.v("Last Rack Name:",rack.rackName)
                    Log.v("Used Quota",rack.usedQuota)
                    usedQuota.onCallback()
                }
                */
                rack.usedQuota = it.size().toString()
                Log.v("Read UsedRackName:",rack.rackName)
                Log.v("Used Quota",rack.usedQuota)
                if(rack.equals(last)){

                    usedQuota.onCallback()
                }


            }.addOnFailureListener { exception ->

                Log.w("failedAttempt", "Error getting documents.", exception)
            }

        }

       // for(rack in rackData){

       // }
    }

    private fun recyclerView(){
        binding.materialrecycleView.layoutManager = manager
        binding.materialrecycleView.adapter = RecyclerViewAdapter(lowQuotaRack)

    }

    private fun filterLowQuotaRack(){

        for(rack in viewModelData.rack){

            Log.v("Filter Rack Name",rack.rackName)
            Log.v("Test Filter",rack.usedQuota)
            if((rack.quota.toInt() - rack.usedQuota.toInt()) < 10) {
                Log.v("Add to Low List",rack.rackName)
                lowQuotaRack.add(rack)
            }
        }

        Log.v("This is LowQ", lowQuotaRack.size.toString())
        //return lowQuotaRack

    }

    override fun onDestroy() {
        super.onDestroy()
        someHandler?.removeCallbacksAndMessages(null)
        someHandler = null
    }

    override fun onPause() {
        super.onPause()

        rackData.clear()
        lowQuotaRack.clear()

    }

    override fun onResume() {
        super.onResume()


        readMaterial(object : FirestoreCallback {
            override fun onCallback() {

                Log.v("Navigation","1")
                getUsedQuota(object : GetUsedQuota {
                    override fun onCallback() {
                        Log.v("Navigation","2")
                        //viewModelData.rack.clear()

                        filterLowQuotaRack()
                        binding.totalRack.text = viewModelData.rack.size.toString()
                        binding.lowQuotaRack.text = lowQuotaRack.size.toString()
                        recyclerView()
                    }
                })

            }
        })

    }


}