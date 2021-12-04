package com.example.mobileassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.mobileassignment.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val loginEmp = intent.getSerializableExtra("user") as Employee?

        //toolbar
        setSupportActionBar(binding.toolbar)

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
                TODO("navigate to dashboard")
            }
            R.id.nav_listing -> {
                TODO("navigate to listing")
            }
            R.id.nav_add -> {
                TODO("navigate to add material")
            }
            R.id.nav_scanqr -> {
                TODO("navigate to scan qr")
            }
            R.id.logout -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }

        return true
    }
}
