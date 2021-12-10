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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentAddMaterialBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class AddMaterialFragment : Fragment() {

    private lateinit var binding: FragmentAddMaterialBinding
    private lateinit var scannerIntegrator: IntentIntegrator
    private lateinit var qrLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var viewModelData: ViewModelData
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_material, container, false)

        //Create ViewModel (Owner : this activity)
        viewModelData = ViewModelProvider(this).get(ViewModelData::class.java)


        val serial = binding.serialInput.text
        val part = binding.partInput.text
        val qty = binding.quantityInput.text
        val status = binding.statusInput.text
        val rackInDate = binding.dateInput.text
        val emp = binding.receivedInput.text

        //Set Qr Code Scanner
        setupRequiredSetting()

        binding.addButton.setOnClickListener {
            uploadData(serial, part, qty, status, rackInDate, emp)

            Toast.makeText(activity, "Material Added!", Toast.LENGTH_SHORT).show()
            clearText(serial, part, qty, status, rackInDate, emp)
        }

        binding.cancelButton.setOnClickListener{
            clearText(serial, part, qty, status, rackInDate, emp)
        }

        binding.qrButton.setOnClickListener{

            permissionLauncher.launch(android.Manifest.permission.CAMERA)
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

    private fun setDataToView(){

        binding.serialInput.setText(viewModelData.serialNo)
        binding.partInput.setText(viewModelData.partNo)
        binding.quantityInput.setText(viewModelData.quantity)

    }

    private fun setupRequiredSetting(){

        //Set up QR Code Scanner
        setupMyQrScanner()

        //Set up permission
        setupPermissionLauncher()

        //Set up Qr Launcher
        setupQrLauncher()

    }

    private fun setupMyQrScanner(){

        //IntentIntegrator(this) for activity

        val promptText = "Please Scan the OR Code"

        scannerIntegrator = IntentIntegrator.forSupportFragment(this)
        scannerIntegrator.setOrientationLocked(true)
        scannerIntegrator.setPrompt(promptText)

        //0 for back camera
        //1 for front camera
        scannerIntegrator.setCameraId(0)
        scannerIntegrator.setBeepEnabled(true)


    }

    private fun setupQrLauncher(){

        qrLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {
                val result = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(
                            activity,
                            "InCorrect Format!!! Please Try Again",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        try {
                            viewModelData.setValue(JSONObject(result.contents))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                activity,
                                "InCorrect Format!!! Please Try Again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(activity, "InCorrect Format!!! Please Try Again", Toast.LENGTH_LONG)
                        .show()
                }
            }
            //Navigate to Temp Fragment to Show the data
            setDataToView()

        }
    }

    private fun setupPermissionLauncher(){

        //Get Permission
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            if(it){
                qrLauncher.launch(scannerIntegrator.createScanIntent())
                Toast.makeText(activity, "Camera Access Granted !!", Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(activity, "Please Accept Camera Permission !", Toast.LENGTH_LONG).show()
            }
        }
    }


}



