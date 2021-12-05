package com.example.mobileassignment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val rackData: MutableList<Rack>):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {

        holder.rackName.text = rackData[position].rackName.toString()
        holder.quota.text = rackData[position].quota.toString()
        holder.description.text = rackData[position].description.toString()


        if(rackData[position].quota.toInt() <= 10){

            holder.status.text = "Low Quota !!"
            holder.icon.setImageResource(R.drawable.ic_warning)
        }
        else{
            holder.status.text = "Quota Available"
            holder.icon.setImageResource(R.drawable.ic_available)
        }

    }

    override fun getItemCount(): Int {
        return rackData.size
    }

    inner class ViewHolder(view : View):RecyclerView.ViewHolder(view){

        val rackName = view.findViewById<TextView>(R.id.rackName)
        val description = view.findViewById<TextView>(R.id.description)
        val quota = view.findViewById<TextView>(R.id.quota)
        val status = view.findViewById<TextView>(R.id.status)
        val icon = view.findViewById<ImageView>(R.id.StatusIcon)


    }


}