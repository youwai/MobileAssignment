package com.example.mobileassignment

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class todayListAdapter(
    context: Activity,
    private val list: MutableList<Materials>,
    private val indicator: String
) : ArrayAdapter<Materials>(context, R.layout.today_list_item, list) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.today_list_item, null)

        val serialNum = view.findViewById<TextView>(R.id.serialNum)
        val label = view.findViewById<TextView>(R.id.label)

        val labelText = when (indicator) {
            "in" -> "Added by ${list[position].rackInBy}"
            else -> "Retrieved by ${list[position].rackOutBy}"
        }

        serialNum.text = list[position].serialNo
        label.text = labelText

        return view
    }


}