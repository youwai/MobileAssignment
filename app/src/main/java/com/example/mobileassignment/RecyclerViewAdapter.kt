package com.example.mobileassignment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val rackData: MutableList<Rack>):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int){

        holder.rackName.text = rackData[position].rackName
        holder.quota.text = rackData[position].quota
        holder.description.text = rackData[position].description
        Log.v("Test Read The Used Data",rackData[position].usedQuota)


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

        val rackName: TextView = view.findViewById(R.id.rackName)
        val description: TextView = view.findViewById(R.id.description)
        val quota: TextView = view.findViewById(R.id.quota)
        val status: TextView = view.findViewById(R.id.status)
        val icon: ImageView = view.findViewById(R.id.StatusIcon)

        init {
            view.setOnClickListener {

                //This is To Get the position of the cardView.
                val position :Int = absoluteAdapterPosition

                //Navigate to ???

                //rackData[position] Pass This Object to the Material List
                Toast.makeText(view.context, rackData[position].rackName, Toast.LENGTH_LONG).show()
            }
        }
    }

}