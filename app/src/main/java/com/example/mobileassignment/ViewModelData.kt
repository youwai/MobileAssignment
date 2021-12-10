package com.example.mobileassignment

import androidx.lifecycle.ViewModel
import org.json.JSONObject

class ViewModelData: ViewModel() {

    var serialNo : String? = null
    var partNo : String? = null
    var quantity : String? = null
    var rack : MutableList<Rack> = mutableListOf()

    var emp: Employee? = null



    //This is for Qr Data
    fun setValue(contents : JSONObject) {

        resetData()
        serialNo = contents.getString("serialNo")
        partNo = contents.getString("partNo")
        quantity = contents.getString("quantity")

    }

    fun resetData(){
        serialNo = null
        partNo  = null
        quantity = null
    }



}