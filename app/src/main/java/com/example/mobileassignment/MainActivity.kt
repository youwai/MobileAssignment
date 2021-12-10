package com.example.mobileassignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobileassignment.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scannerIntegrator: IntentIntegrator
    private lateinit var qrLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var viewModelData: ViewModelData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Create ViewModel (Owner : this activity)
        viewModelData = ViewModelProvider(this).get(ViewModelData::class.java)

        val loginEmp = intent.getSerializableExtra("user") as Employee?
        viewModelData.emp = loginEmp

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //navigation drawer menu configuration
        binding.navView.bringToFront()
        binding.navView.itemIconTintList = null
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // change "logout" word color
        val menu = binding.navView.menu
        val text = menu.findItem(R.id.logout)
        val s = SpannableString(text.title)
        s.setSpan(TextAppearanceSpan(this, R.style.logout_text),0, s.length, 0)
        text.title = s

        // onClickListener navigation bar items
        binding.navView.setNavigationItemSelectedListener(this)

        // to change the employee name and id
        val header = binding.navView.getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.username)
        val empID = header.findViewById<TextView>(R.id.empId)

        name.text = loginEmp?.name
        empID.text = loginEmp?.id

        //Set Qr Code Scanner
        setupRequiredSetting()

    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_dashboard -> {

//                Navigate To Dashboard
                navigationFunction(Dashboard())

                // Do not delete. This is to close the navigation slider
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_listing -> {

                //Navigate to List
                navigationFunction(RackList())

                // Do not delete. This is to close the navigation slider
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_add -> {

                //Navigate to Add Materials
                navigationFunction(AddMaterialFragment())

                // Do not delete. This is to close the navigation slider
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_scanqr -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.logout -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }

        return true
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

        scannerIntegrator = IntentIntegrator(this)
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
                            this,
                            "InCorrect Format!!! Please Try Again",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        try {
                            viewModelData.setValue(JSONObject(result.contents))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "InCorrect Format!!! Please Try Again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "InCorrect Format!!! Please Try Again", Toast.LENGTH_LONG)
                        .show()
                }
            }
            //Navigate to Temp Fragment to Show the data
            navigationFunction(TempFragment())
        }
    }

    private fun setupPermissionLauncher(){

        //Get Permission
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            if(it){
                qrLauncher.launch(scannerIntegrator.createScanIntent())
                Toast.makeText(this, "Camera Access Granted !!", Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(this, "Please Accept Camera Permission !", Toast.LENGTH_LONG).show()
            }

        }

    }

    //This Function is call to navigate to other fragment
    private fun navigationFunction(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .addToBackStack(null)
            .commit()

    }



}
