package com.example.mobileassignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mobileassignment.databinding.FragmentAddMaterialBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddMaterialFragment : Fragment() {

    private lateinit var binding: FragmentAddMaterialBinding
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_material, container, false)

        val serial = binding.serialInput.text
        val part = binding.partInput.text
        val qty = binding.quantityInput.text
        val status = binding.statusInput.text
        val rackInDate = binding.dateInput.text
        val emp = binding.receivedInput.text

        binding.addButton.setOnClickListener {
            uploadData(serial, part, qty, status, rackInDate, emp)

            Toast.makeText(activity, "Material Added!", Toast.LENGTH_SHORT).show()
            clearText(serial, part, qty, status, rackInDate, emp)
        }

        binding.cancelButton.setOnClickListener{
            clearText(serial, part, qty, status, rackInDate, emp)
        }

        binding.qrButton.setOnClickListener{

        }

        return binding.root
    }

    private fun uploadData(serial: Editable?, part: Editable?, qty: Editable?, status: Editable?, rackInDate: Editable?, emp: Editable?){
        val hashMap = hashMapOf<String, Any>(
            "serialNo" to serial.toString(),
            "partNo" to part.toString(),
            "quantity" to qty.toString(),
            "status" to status.toString(),
            "rackInDate" to rackInDate.toString(),
            "rackInBy" to emp.toString(),
            "rackOutBy" to "",
            "rackOutDate" to ""
        )
        val rackPath = when(part.toString().first()){
            'A' -> "Rack1"
            'B' -> "Rack2"
            'C' -> "Rack3"
            'D' -> "Rack4"
            'E' -> "Rack5"
            'F' -> "Rack6"
            'G' -> "Rack7"
            'H' -> "Rack8"
            else -> "Rack9"
        }
        db.collection("Rack").document(rackPath).collection("Materials").document(serial.toString())
            .set(hashMap)
    }

    private fun clearText(serial: Editable?, part: Editable?, qty: Editable?, status: Editable?, rackInDate: Editable?, emp: Editable?){
        serial?.clear()
        part?.clear()
        qty?.clear()
        status?.clear()
        rackInDate?.clear()
        emp?.clear()
    }

}



