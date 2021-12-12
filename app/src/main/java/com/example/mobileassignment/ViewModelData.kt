package com.example.mobileassignment

import androidx.lifecycle.ViewModel
import org.json.JSONObject

class ViewModelData: ViewModel() {

    var serialNo : String? = null
    var partNo : String? = null
    var quantity : String? = null
    var todayInMaterial :MutableList<Materials> = mutableListOf()
    var todayOutMaterial :MutableList<Materials> = mutableListOf()
    var emp: Employee? = null

    //This is for Qr Data
    fun setValue(contents : JSONObject) {

        resetQrData()
        serialNo = contents.getString("serialNo")
        partNo = contents.getString("partNo")
        quantity = contents.getString("quantity")

    }

    fun resetQrData(){
        serialNo = null
        partNo  = null
        quantity = null
    }

    fun resetDbData(){
        todayInMaterial.clear()
        todayOutMaterial.clear()
    }


}