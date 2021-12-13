package com.example.mobileassignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassignment.databinding.FragmentAddMaterialBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddMaterialFragment : Fragment() {

    private lateinit var binding: FragmentAddMaterialBinding
    private lateinit var scannerIntegrator: IntentIntegrator
    private lateinit var qrLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var viewModelData: ViewModelData
    private lateinit var rack: Rack
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_material, container, false)

        //Create ViewModel (Owner : this activity)
        viewModelData = ViewModelProvider(requireActivity()).get(ViewModelData::class.java)

        //get Today's Date as Rack In Date and Employee ID
        getRackInDate()
        getEmpID()

        //Set Status to 2
        binding.statusInput.setText("2")

        //Set Qr Code Scanner
        setupRequiredSetting()

        binding.qrButton.setOnClickListener {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        binding.addButton.setOnClickListener {
            val serial = binding.serialInputLayout.editText?.text
            val part = binding.partInputLayout.editText?.text
            val qty = binding.quantityInputLayout.editText?.text
            val status = binding.statusInputLayout.editText?.text
            val rackInDate = binding.dateInputLayout.editText?.text
            val emp = binding.receivedInputLayout.editText?.text

            //remove error field after changing text in input field
            binding.serialInput.doOnTextChanged { _, _, _, _ ->
                binding.serialInputLayout.error = null
            }

            binding.partInput.doOnTextChanged { _, _, _, _ ->
                binding.partInputLayout.error = null
            }

            binding.quantityInput.doOnTextChanged { _, _, _, _ ->
                binding.quantityInputLayout.error = null
            }

            //if input field is empty, show error
            if (serial?.isEmpty() == true) {
                binding.serialInputLayout.error = " "
            }
            if (part?.isEmpty() == true) {
                binding.partInputLayout.error = " "
            }
            if (qty?.isEmpty() == true) {
                binding.quantityInputLayout.error = " "
            }
            //if no input field are empty, upload data to firebase and clear text field later
            if (serial?.isEmpty() == false && part?.isEmpty() == false && qty?.isEmpty() == false) {
                uploadData(serial, part, qty, status, rackInDate, emp)
                binding.serialInput.text?.clear()
                binding.partInput.text?.clear()
                binding.quantityInput.text?.clear()
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.serialInput.text?.clear()
            binding.partInput.text?.clear()
            binding.quantityInput.text?.clear()
        }

        return binding.root
    }

    private fun uploadData(
        serial: Editable?,
        part: Editable?,
        qty: Editable?,
        status: Editable?,
        rackInDate: Editable?,
        emp: Editable?
    ) {
        val hashMap = hashMapOf<String, Any>(
            "serialNo" to serial.toString(),
            "partNo" to part.toString(),
            "quantity" to qty.toString().toInt(),
            "status" to status.toString().toInt(),
            "rackInDate" to rackInDate.toString(),
            "rackInBy" to emp.toString(),
            "rackOutBy" to "",
            "rackOutDate" to ""
        )
        val rackPath = when (part.toString().first()) {
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

        for (racks in viewModelData.racks) {

            if (rackPath.equals(racks.rackName)) {

                Log.v("Check", rackPath.equals(racks.rackName).toString())
                rack = racks

            }
        }

        if (rack.usedQuota.toInt() < 30) {
            Log.v("Check", rack.usedQuota)
            db.collection("Rack").document(rackPath).collection("Materials")
                .document(serial.toString())
                .set(hashMap).addOnSuccessListener {

                    Snackbar.make(
                        requireView(),
                        "Material Added to $rackPath!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some error had occurred!", Toast.LENGTH_SHORT).show()
                }
        } else {

            Snackbar.make(requireView(), "No Enough Quota In $rackPath!", Snackbar.LENGTH_LONG)
                .show()

        }

    }


    private fun setDataToView() {
        binding.serialInput.setText(viewModelData.serialNo)
        binding.partInput.setText(viewModelData.partNo)
        binding.quantityInput.setText(viewModelData.quantity)

    }

    private fun getEmpID() {

        val id = viewModelData.emp?.id.toString()

        binding.receivedInput.setText(id)

    }

    private fun getRackInDate() {

        val date = SimpleDateFormat("dd/MM/yyyy").format(Date())

        binding.dateInput.setText(date)


    }

    private fun setupRequiredSetting() {

        //Set up QR Code Scanner
        setupMyQrScanner()

        //Set up permission
        setupPermissionLauncher()

        //Set up Qr Launcher
        setupQrLauncher()

    }

    private fun setupMyQrScanner() {

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

    private fun setupQrLauncher() {

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
                    Toast.makeText(
                        activity,
                        "InCorrect Format!!! Please Try Again",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
            //Navigate to Temp Fragment to Show the data
            setDataToView()
        }
    }

    private fun setupPermissionLauncher() {

        //Get Permission
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {

                if (it) {
                    qrLauncher.launch(scannerIntegrator.createScanIntent())
                    Toast.makeText(activity, "Camera Access Granted !!", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(activity, "Please Accept Camera Permission !", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}



