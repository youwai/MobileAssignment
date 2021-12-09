package com.example.mobileassignment

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class MaterialListAdapter(context: Activity, private val materials: ArrayList<Materials>) :
    ArrayAdapter<Materials>(context, R.layout.material_list_item, materials) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.material_list_item, null)

        val serialNum = view.findViewById<TextView>(R.id.serialNum)

        serialNum.text = materials[position].serialNo

        return view
    }
}