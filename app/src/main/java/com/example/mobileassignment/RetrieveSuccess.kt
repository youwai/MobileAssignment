package com.example.mobileassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentRetrieveSuccessBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RetrieveSuccess(private val record: Materials?) : Fragment() {

    private lateinit var binding: FragmentRetrieveSuccessBinding
    private lateinit var viewModelData: ViewModelData
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_retrieve_success, container, false)
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        binding.viewmodelData = viewModelData

        getRetrieveBy()
        getRetrieveDate()
        updateRecord()

        return binding.root
    }

    //Get the employee ID that retrieve the material
    private fun getRetrieveBy() {

        var id = viewModelData.emp?.id.toString()

        binding.rbInput.setText(id)

    }

    //Get the date of retrieval
    private fun getRetrieveDate() {

        var date = SimpleDateFormat("dd/MM/yyyy").format(Date())

        binding.rdInput.setText(date)


    }

    //Update the data in database
    private fun updateRecord() {

        val hashMap = hashMapOf<String, Any>(
            "serialNo" to viewModelData.serialNo.toString(),
            "partNo" to viewModelData.partNo.toString(),
            "quantity" to viewModelData.quantity.toString().toInt(),
            "status" to record?.status.toString().toInt().plus(1),
            "rackInBy" to record?.rackInBy.toString(),
            "rackInDate" to record?.rackInDate.toString(),
            "rackOutBy" to binding.rbInput.text.toString(),
            "rackOutDate" to binding.rdInput.text.toString()
        )

        var rackName = when (viewModelData.partNo?.first()) {
            'A' -> "Rack1"
            'B' -> "Rack2"
            'C' -> "Rack3"
            'D' -> "Rack4"
            'E' -> "Rack5"
            'F' -> "Rack6"
            'G' -> "Rack7"
            'H' -> "Rack8"
            'I' -> "Rack9"
            'J' -> "Rack10"
            else -> "Rack11"
        }

        db.collection("Rack").document(rackName).collection("Materials")
            .document(viewModelData.serialNo.toString()).set(hashMap).addOnSuccessListener {
                Toast.makeText(activity, "Updated Database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error !", Toast.LENGTH_SHORT).show()
            }

        //Button will lead user to main page
        binding.buttonRetrieveSuccess.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, Dashboard())
//                .addToBackStack(null)
//                .commit()

            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

    }
}