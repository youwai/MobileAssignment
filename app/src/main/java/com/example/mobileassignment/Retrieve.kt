package com.example.mobileassignment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.ActivityRetrieveBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Retrieve : Fragment() {

    private lateinit var binding: ActivityRetrieveBinding
    private lateinit var viewModelData: ViewModelData
    private val db = Firebase.firestore
    private var record: Materials? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater, R.layout.activity_retrieve, container, false)
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        binding.viewmodelData = viewModelData

        var rackName = when (viewModelData.partNo?.first()) {
            'A' -> "Rack1"
            'B' -> "Rack2"
            'C' -> "Rack3"
            'D' -> "Rack4"
            'E' -> "Rack5"
            'F' -> "Rack6"
            'G' -> "Rack7"
            'H' -> "Rack8"
            else -> "Invalid Rack"
        }

        binding.rackInput.setText(rackName)

        getStatus(rackName, object : FirestoreCallback {
            override fun onCallback(item: Materials?) {
                Log.d("status", record.toString())

                binding.statusInput.setText(record?.status.toString())

                val stat = binding.statusLayout.editText?.text.toString()

                if (stat == "2") {
                    binding.retrieveButton.setOnClickListener {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, RetrieveSuccess(record))
                            .addToBackStack(null)
                            .commit()
                    }

                }
                else{
                    binding.retrieveButton.setOnClickListener{
                        val dialogRB = DialogRetrieveFail()

                        dialogRB.show(requireActivity().supportFragmentManager, "customDialog")

                    }
                }


            }
        })


        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return binding.root
    }

    private fun getStatus(rackName: String, firestoreCallback: FirestoreCallback) {

        val srIn = viewModelData.serialNo

        val ref = db.collection("Rack").document(rackName).collection("Materials")

        ref.get().addOnSuccessListener {

            for (doc in it) {
                if (doc.data["serialNo"].toString().equals(srIn)) {
                    record = doc.toObject<Materials>()

                    break
                }


            }
            firestoreCallback.onCallback(record)

        }

            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                Toast.makeText(activity,"Error finding data in database !",Toast.LENGTH_SHORT).show()
            }

    }

    private interface FirestoreCallback {
        fun onCallback(item: Materials?)

    }


}
