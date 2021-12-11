package com.example.mobileassignment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private var rackData: MutableList<Rack>):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(), Filterable{

    var tempRackData : MutableList<Rack> = mutableListOf()

    init {
        tempRackData.addAll(rackData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int){

        holder.availableQuota = rackData[position].quota.toInt() - rackData[position].usedQuota.toInt()
        holder.rackName.text = rackData[position].rackName
        holder.description.text = rackData[position].description
        holder.quota.text = holder.availableQuota.toString()

        if(holder.availableQuota <= 10){

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
        var availableQuota : Int = 0

        init {
            view.setOnClickListener {

                //This is To Get the position of the cardView.
                val position :Int = absoluteAdapterPosition

                //Navigate to ???
                val activity = view.context as MainActivity
                val fragment = RackDetails(rackData[position])
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
                Log.v("Navigation","Navigate")
                //rackData[position] Pass This Object to the Material List
                Toast.makeText(view.context, rackData[position].rackName, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getFilter(): Filter {

        return listFilter
    }


    private val listFilter = object: Filter(){

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val filterList = FilterResults()

            if(constraint == null || constraint.isEmpty()){

                filterList.count = tempRackData.size
                filterList.values = tempRackData
            }
            else{
                val filterPattern = constraint.toString().lowercase()

                val racks : MutableList<Rack> = mutableListOf()

                for(rack in tempRackData){
                    if(rack.rackName.lowercase().contains(filterPattern)){
                        racks.add(rack)
                    }
                }
                filterList.count = racks.size
                filterList.values = racks
            }

            return filterList
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            rackData = results!!.values as MutableList<Rack>
            notifyDataSetChanged()

        }



    }


}